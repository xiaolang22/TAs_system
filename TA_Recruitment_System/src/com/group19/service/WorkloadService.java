package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.AssignedPositionDto;
import com.group19.dto.ServiceResult;
import com.group19.dto.TaWorkloadRow;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.TA;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WorkloadService {
    private static final int DEFAULT_MAX_WEEKLY_WORKLOAD_HOURS = 20;
    private static final Pattern FIRST_NUMBER = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final Pattern TIME_RANGE_PATTERN = Pattern.compile(
            "\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\s*(?:-|to)\\s*(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DAY_PATTERN = Pattern.compile(
            "\\b(mon(?:day)?s?|tue(?:sday)?s?|wed(?:nesday)?s?|thu(?:rsday)?s?|fri(?:day)?s?|sat(?:urday)?s?|sun(?:day)?s?)\\b",
            Pattern.CASE_INSENSITIVE);
    private final TADao taDao;
    private final ApplicationDao applicationDao;
    private final JobDao jobDao;
    private final int maxWeeklyWorkloadHours;

    public WorkloadService(TADao taDao, ApplicationDao applicationDao, JobDao jobDao) {
        this(taDao, applicationDao, jobDao, DEFAULT_MAX_WEEKLY_WORKLOAD_HOURS);
    }

    public WorkloadService(TADao taDao, ApplicationDao applicationDao, JobDao jobDao, int maxWeeklyWorkloadHours) {
        this.taDao = taDao;
        this.applicationDao = applicationDao;
        this.jobDao = jobDao;
        this.maxWeeklyWorkloadHours = maxWeeklyWorkloadHours > 0
                ? maxWeeklyWorkloadHours
                : DEFAULT_MAX_WEEKLY_WORKLOAD_HOURS;
    }

    public ServiceResult<List<TaWorkloadRow>> loadWorkloadRows(String keyword, String assignmentFilter) {
        try {
            Map<String, TaWorkloadRow> rowsByStudentId = buildRowsByStudentId(taDao.findAll());
            Map<String, Job> jobsById = buildJobsById(jobDao.findAll());

            for (Application application : applicationDao.findAll()) {
                if (!isAccepted(application)) {
                    continue;
                }

                String studentKey = normalizeKey(application.getTaStudentId());
                if (studentKey.isEmpty()) {
                    continue;
                }

                TaWorkloadRow row = rowsByStudentId.get(studentKey);
                if (row == null) {
                    row = buildFallbackRow(application);
                    rowsByStudentId.put(studentKey, row);
                }

                Job job = jobsById.get(normalizeKey(application.getJobId()));
                AssignedPositionDto position = buildAssignedPosition(application, job);
                row.getAssignedPositions().add(position);
                if (position.isHoursCounted()) {
                    row.setTotalAssignedHours(row.getTotalAssignedHours() + position.getParsedHours());
                }
            }

            List<TaWorkloadRow> rows = new ArrayList<>(rowsByStudentId.values());
            for (TaWorkloadRow row : rows) {
                row.setAssignedPositionCount(row.getAssignedPositions().size());
                row.setTotalAssignedHoursLabel(formatHours(row.getTotalAssignedHours()));
                applyWorkloadWarnings(row);
                row.getAssignedPositions().sort(Comparator
                        .comparing((AssignedPositionDto position) -> safe(position.getTitle()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(position -> safe(position.getJobId()), String.CASE_INSENSITIVE_ORDER));
            }

            List<TaWorkloadRow> filteredRows = filterRows(rows, keyword, assignmentFilter);
            filteredRows.sort(Comparator
                    .comparingDouble(TaWorkloadRow::getTotalAssignedHours).reversed()
                    .thenComparing(row -> safe(row.getName()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(row -> safe(row.getStudentId()), String.CASE_INSENSITIVE_ORDER));

            return ServiceResult.success(filteredRows, "Workload data loaded.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to load TA profile data.");
        }
    }

    private static Map<String, TaWorkloadRow> buildRowsByStudentId(List<TA> tas) {
        Map<String, TaWorkloadRow> rowsByStudentId = new LinkedHashMap<>();
        if (tas == null) {
            return rowsByStudentId;
        }

        int fallbackIndex = 0;
        for (TA ta : tas) {
            TaWorkloadRow row = new TaWorkloadRow();
            row.setStudentId(safe(ta.getStudentId()));
            row.setName(firstNonBlank(ta.getName(), "Unknown TA"));
            row.setEmail(safe(ta.getEmail()));
            row.setProgramme(safe(ta.getProgramme()));
            row.setTotalAssignedHours(0);
            row.setTotalAssignedHoursLabel(formatHours(0));

            String key = normalizeKey(ta.getStudentId());
            if (key.isEmpty()) {
                key = "ta-profile-" + fallbackIndex;
                fallbackIndex++;
            }
            rowsByStudentId.put(key, row);
        }
        return rowsByStudentId;
    }

    private static Map<String, Job> buildJobsById(List<Job> jobs) {
        Map<String, Job> jobsById = new HashMap<>();
        if (jobs == null) {
            return jobsById;
        }
        for (Job job : jobs) {
            String key = normalizeKey(job.getJobId());
            if (!key.isEmpty()) {
                jobsById.put(key, job);
            }
        }
        return jobsById;
    }

    private static TaWorkloadRow buildFallbackRow(Application application) {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setStudentId(safe(application.getTaStudentId()));
        row.setName(firstNonBlank(application.getTaName(), "Unknown TA"));
        row.setEmail("");
        row.setProgramme("");
        row.setTotalAssignedHours(0);
        row.setTotalAssignedHoursLabel(formatHours(0));
        return row;
    }

    private static AssignedPositionDto buildAssignedPosition(Application application, Job job) {
        AssignedPositionDto position = new AssignedPositionDto();
        position.setApplicationId(safe(application.getApplicationId()));
        position.setJobId(safe(application.getJobId()));

        if (job == null) {
            position.setTitle("Unknown position");
            position.setCategory("");
            position.setHoursText("");
            position.setScheduleText("");
            position.setParsedHours(0);
            position.setHoursCounted(false);
            return position;
        }

        position.setTitle(firstNonBlank(job.getTitle(), "Untitled position"));
        position.setCategory(safe(job.getCategory()));
        position.setHoursText(safe(job.getHours()));
        position.setScheduleText(safe(job.getSchedule()));

        Double parsedHours = parseHours(job.getHours());
        if (parsedHours == null) {
            position.setParsedHours(0);
            position.setHoursCounted(false);
        } else {
            position.setParsedHours(parsedHours);
            position.setHoursCounted(true);
        }
        return position;
    }

    private void applyWorkloadWarnings(TaWorkloadRow row) {
        List<String> reasons = new ArrayList<>();
        if (markTimeConflicts(row.getAssignedPositions())) {
            reasons.add("Time conflict");
        }
        if (row.getTotalAssignedHours() > maxWeeklyWorkloadHours) {
            reasons.add("Hours limit exceeded");
        }
        row.setWorkloadWarningReasons(reasons);
    }

    private static boolean markTimeConflicts(List<AssignedPositionDto> positions) {
        if (positions == null || positions.size() < 2) {
            return false;
        }

        List<PositionSchedule> schedules = new ArrayList<>();
        for (AssignedPositionDto position : positions) {
            position.setTimeConflict(false);
            List<ScheduleSlot> slots = parseScheduleSlots(position.getScheduleText());
            if (!slots.isEmpty()) {
                schedules.add(new PositionSchedule(position, slots));
            }
        }

        boolean hasConflict = false;
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                PositionSchedule first = schedules.get(i);
                PositionSchedule second = schedules.get(j);
                if (hasScheduleConflict(first.slots, second.slots)) {
                    first.position.setTimeConflict(true);
                    second.position.setTimeConflict(true);
                    hasConflict = true;
                }
            }
        }
        return hasConflict;
    }

    private static boolean hasScheduleConflict(List<ScheduleSlot> firstSlots, List<ScheduleSlot> secondSlots) {
        for (ScheduleSlot first : firstSlots) {
            for (ScheduleSlot second : secondSlots) {
                if (first.overlaps(second)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<ScheduleSlot> parseScheduleSlots(String schedule) {
        if (schedule == null || schedule.isBlank()) {
            return new ArrayList<>();
        }

        List<ScheduleSlot> segmentedSlots = new ArrayList<>();
        String[] segments = schedule.split("[;\\n]");
        if (segments.length > 1) {
            for (String segment : segments) {
                segmentedSlots.addAll(parseScheduleSegment(segment));
            }
            if (!segmentedSlots.isEmpty()) {
                return segmentedSlots;
            }
        }
        return parseScheduleSegment(schedule);
    }

    private static List<ScheduleSlot> parseScheduleSegment(String schedule) {
        List<ScheduleSlot> slots = new ArrayList<>();
        List<DayOfWeek> days = extractDays(schedule);
        List<TimeWindow> windows = extractTimeWindows(schedule);
        if (days.isEmpty() || windows.isEmpty()) {
            return slots;
        }

        for (DayOfWeek day : days) {
            for (TimeWindow window : windows) {
                slots.add(new ScheduleSlot(day, window.startMinutes, window.endMinutes));
            }
        }
        return slots;
    }

    private static List<DayOfWeek> extractDays(String schedule) {
        Set<DayOfWeek> days = new LinkedHashSet<>();
        Matcher matcher = DAY_PATTERN.matcher(safe(schedule).toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            DayOfWeek day = parseDay(matcher.group(1));
            if (day != null) {
                days.add(day);
            }
        }
        return new ArrayList<>(days);
    }

    private static DayOfWeek parseDay(String rawDay) {
        String normalized = normalizeKey(rawDay);
        if (normalized.startsWith("mon")) {
            return DayOfWeek.MONDAY;
        }
        if (normalized.startsWith("tue")) {
            return DayOfWeek.TUESDAY;
        }
        if (normalized.startsWith("wed")) {
            return DayOfWeek.WEDNESDAY;
        }
        if (normalized.startsWith("thu")) {
            return DayOfWeek.THURSDAY;
        }
        if (normalized.startsWith("fri")) {
            return DayOfWeek.FRIDAY;
        }
        if (normalized.startsWith("sat")) {
            return DayOfWeek.SATURDAY;
        }
        if (normalized.startsWith("sun")) {
            return DayOfWeek.SUNDAY;
        }
        return null;
    }

    private static List<TimeWindow> extractTimeWindows(String schedule) {
        List<TimeWindow> windows = new ArrayList<>();
        Matcher matcher = TIME_RANGE_PATTERN.matcher(safe(schedule).toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            TimeWindow window = parseTimeWindow(matcher);
            if (window != null) {
                windows.add(window);
            }
        }
        return windows;
    }

    private static TimeWindow parseTimeWindow(Matcher matcher) {
        int startHour = parseIntOrDefault(matcher.group(1), -1);
        int startMinute = parseIntOrDefault(matcher.group(2), 0);
        int endHour = parseIntOrDefault(matcher.group(4), -1);
        int endMinute = parseIntOrDefault(matcher.group(5), 0);
        String endSuffix = normalizeKey(matcher.group(6));
        String startSuffix = normalizeKey(matcher.group(3));
        if (startSuffix.isEmpty()) {
            startSuffix = inferStartSuffix(startHour, endHour, endSuffix);
        }

        int start = toMinutes(startHour, startMinute, startSuffix);
        int end = toMinutes(endHour, endMinute, endSuffix);
        if (start < 0 || end < 0) {
            return null;
        }
        if (end <= start) {
            end += 12 * 60;
        }
        if (end <= start || end - start > 12 * 60) {
            return null;
        }
        return new TimeWindow(start, end);
    }

    private static String inferStartSuffix(int startHour, int endHour, String endSuffix) {
        if ("pm".equals(endSuffix) && (startHour > endHour || (endHour == 12 && startHour < 12))) {
            return "am";
        }
        return endSuffix;
    }

    private static int toMinutes(int hour, int minute, String suffix) {
        if (hour < 1 || hour > 12 || minute < 0 || minute > 59 || suffix == null || suffix.isBlank()) {
            return -1;
        }
        int normalizedHour;
        if ("am".equals(suffix)) {
            normalizedHour = hour == 12 ? 0 : hour;
        } else if ("pm".equals(suffix)) {
            normalizedHour = hour == 12 ? 12 : hour + 12;
        } else {
            return -1;
        }
        return normalizedHour * 60 + minute;
    }

    private static List<TaWorkloadRow> filterRows(List<TaWorkloadRow> rows, String keyword, String assignmentFilter) {
        String normalizedKeyword = normalizeKey(keyword);
        String normalizedFilter = normalizeKey(assignmentFilter);
        return rows.stream()
                .filter(row -> matchesKeyword(row, normalizedKeyword))
                .filter(row -> matchesAssignmentFilter(row, normalizedFilter))
                .collect(Collectors.toList());
    }

    private static boolean matchesKeyword(TaWorkloadRow row, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        if (fieldContains(row.getName(), keyword)
                || fieldContains(row.getStudentId(), keyword)
                || fieldContains(row.getEmail(), keyword)
                || fieldContains(row.getProgramme(), keyword)) {
            return true;
        }
        for (AssignedPositionDto position : row.getAssignedPositions()) {
            if (fieldContains(position.getTitle(), keyword) || fieldContains(position.getCategory(), keyword)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesAssignmentFilter(TaWorkloadRow row, String assignmentFilter) {
        if ("assigned".equals(assignmentFilter)) {
            return row.getAssignedPositionCount() > 0;
        }
        if ("unassigned".equals(assignmentFilter)) {
            return row.getAssignedPositionCount() == 0;
        }
        return true;
    }

    private static Double parseHours(String hoursText) {
        if (hoursText == null || hoursText.isBlank()) {
            return null;
        }
        Matcher matcher = FIRST_NUMBER.matcher(hoursText);
        if (!matcher.find()) {
            return null;
        }
        try {
            return Double.parseDouble(matcher.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isAccepted(Application application) {
        return application != null && "accepted".equals(normalizeKey(application.getStatus()));
    }

    private static boolean fieldContains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private static int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static String formatHours(double hours) {
        return BigDecimal.valueOf(hours)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString() + " hours";
    }

    private static String firstNonBlank(String first, String fallback) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        return fallback;
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static class PositionSchedule {
        private final AssignedPositionDto position;
        private final List<ScheduleSlot> slots;

        private PositionSchedule(AssignedPositionDto position, List<ScheduleSlot> slots) {
            this.position = position;
            this.slots = slots;
        }
    }

    private static class ScheduleSlot {
        private final DayOfWeek day;
        private final int startMinutes;
        private final int endMinutes;

        private ScheduleSlot(DayOfWeek day, int startMinutes, int endMinutes) {
            this.day = day;
            this.startMinutes = startMinutes;
            this.endMinutes = endMinutes;
        }

        private boolean overlaps(ScheduleSlot other) {
            return other != null
                    && day == other.day
                    && startMinutes < other.endMinutes
                    && other.startMinutes < endMinutes;
        }
    }

    private static class TimeWindow {
        private final int startMinutes;
        private final int endMinutes;

        private TimeWindow(int startMinutes, int endMinutes) {
            this.startMinutes = startMinutes;
            this.endMinutes = endMinutes;
        }
    }
}
