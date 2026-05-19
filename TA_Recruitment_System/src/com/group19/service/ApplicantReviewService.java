package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.ApplicantReviewPageData;
import com.group19.dto.ApplicantReviewRow;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.TA;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ApplicantReviewService {
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "be", "by", "for", "from", "good", "help", "helping",
            "in", "is", "it", "knowledge", "of", "on", "or", "previous", "preferred", "proficiency",
            "skill", "skills", "strong", "support", "the", "to", "with", "able", "ability", "experience",
            "basic", "basics", "maintain", "maintaining", "reliable", "punctual");
    private static final int DEFAULT_MAX_WEEKLY_WORKLOAD_HOURS = 20;
    private static final Pattern HOURS_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final Pattern TIME_RANGE_PATTERN = Pattern.compile(
            "\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\s*(?:-|to)\\s*(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DAY_PATTERN = Pattern.compile(
            "\\b(mon(?:day)?s?|tue(?:sday)?s?|wed(?:nesday)?s?|thu(?:rsday)?s?|fri(?:day)?s?|sat(?:urday)?s?|sun(?:day)?s?)\\b",
            Pattern.CASE_INSENSITIVE);

    private final ApplicationDao applicationDao;
    private final TADao taDao;
    private final JobDao jobDao;
    private final int maxWeeklyWorkloadHours;

    public ApplicantReviewService(ApplicationDao applicationDao, TADao taDao, JobDao jobDao) {
        this(applicationDao, taDao, jobDao, DEFAULT_MAX_WEEKLY_WORKLOAD_HOURS);
    }

    public ApplicantReviewService(ApplicationDao applicationDao, TADao taDao, JobDao jobDao, int maxWeeklyWorkloadHours) {
        this.applicationDao = applicationDao;
        this.taDao = taDao;
        this.jobDao = jobDao;
        this.maxWeeklyWorkloadHours = maxWeeklyWorkloadHours > 0
                ? maxWeeklyWorkloadHours
                : DEFAULT_MAX_WEEKLY_WORKLOAD_HOURS;
    }

    public ServiceResult<ApplicantReviewPageData> loadApplicantsForJob(String jobId, String sortMode) {
        if (jobId == null || jobId.isBlank()) {
            return ServiceResult.failure("Job ID is required.");
        }

        Job job = jobDao.findById(jobId.trim());
        if (job == null) {
            return ServiceResult.failure("Job not found.");
        }

        String normalizedSortMode = normalizeSortMode(sortMode);
        Map<String, TA> taMap = loadTaMap();
        Map<String, WorkloadInfo> workloadMap = buildWorkloadMap();
        List<String> requirementTokens = tokenize(job.getRequirements());

        List<ApplicantReviewRow> rows = new ArrayList<>();
        for (Application application : applicationDao.findByJobId(job.getJobId())) {
            rows.add(buildRow(application, taMap.get(normalizeKey(application.getTaStudentId())),
                    requirementTokens, workloadMap.getOrDefault(normalizeKey(application.getTaStudentId()), new WorkloadInfo())));
        }

        sortRows(rows, normalizedSortMode);

        ApplicantReviewPageData pageData = new ApplicantReviewPageData();
        pageData.setJob(job);
        pageData.setApplicants(rows);
        pageData.setSortMode(normalizedSortMode);
        pageData.setSortLabel("status".equals(normalizedSortMode) ? "Status" : "Match degree");
        return ServiceResult.success(pageData, "Applicant review data loaded.");
    }

    private ApplicantReviewRow buildRow(
            Application application,
            TA profile,
            List<String> requirementTokens,
            WorkloadInfo workloadInfo) {
        ApplicantReviewRow row = new ApplicantReviewRow();
        row.setApplicationId(valueOrEmpty(application.getApplicationId()));
        row.setJobId(valueOrEmpty(application.getJobId()));
        row.setTaStudentId(valueOrEmpty(application.getTaStudentId()));
        row.setTaName(firstNonBlank(profile == null ? null : profile.getName(), application.getTaName()));
        row.setCvFilePath(firstNonBlank(profile == null ? null : profile.getCvFilePath(), application.getCvFilePath()));
        row.setStatus(valueOrEmpty(application.getStatus()));
        row.setSubmittedAt(valueOrEmpty(application.getSubmittedAt()));
        row.setUpdatedAt(valueOrEmpty(application.getUpdatedAt()));
        row.setDecisionNote(valueOrEmpty(application.getDecisionNote()));
        row.setProgramme(valueOrEmpty(profile == null ? null : profile.getProgramme()));
        row.setSkills(valueOrEmpty(profile == null ? null : profile.getSkills()));
        row.setExperience(valueOrEmpty(profile == null ? null : profile.getExperience()));
        row.setAvailability(valueOrEmpty(profile == null ? null : profile.getAvailability()));
        row.setCurrentWorkload(workloadInfo.applicationCount);
        row.setTotalWorkloadHours(workloadInfo.totalHours);
        row.setCurrentWorkloadLabel(buildWorkloadLabel(workloadInfo));
        row.setWorkloadWarningReasons(buildWorkloadWarningReasons(workloadInfo, row.getJobId()));

        List<String> skillTokens = tokenize(row.getSkills());
        int score = calculateMatchScore(requirementTokens, skillTokens);
        row.setMatchScore(score);
        row.setCoreSkillsHtml(buildCoreSkillsHtml(row.getSkills(), requirementTokens));
        row.setMatchedSkillsText(joinTokens(intersection(requirementTokens, skillTokens)));
        row.setMissingSkillsText(joinTokens(difference(requirementTokens, skillTokens)));
        applySkillStatus(row, profile, score);
        return row;
    }

    private Map<String, TA> loadTaMap() {
        Map<String, TA> taMap = new HashMap<>();
        try {
            for (TA ta : taDao.findAll()) {
                if (ta.getStudentId() != null && !ta.getStudentId().isBlank()) {
                    taMap.put(normalizeKey(ta.getStudentId()), ta);
                }
            }
        } catch (IOException ignored) {
            // Continue with application records when TA data cannot be loaded.
        }
        return taMap;
    }

    private Map<String, WorkloadInfo> buildWorkloadMap() {
        Map<String, Job> jobMap = new HashMap<>();
        for (Job job : jobDao.findAll()) {
            String jobId = normalizeKey(job.getJobId());
            if (!jobId.isEmpty()) {
                jobMap.put(jobId, job);
            }
        }

        Map<String, List<WorkloadAssignment>> assignmentsByTa = new HashMap<>();
        for (Application application : applicationDao.findAll()) {
            if (!isActiveStatus(application.getStatus())) {
                continue;
            }
            String studentId = normalizeKey(application.getTaStudentId());
            if (studentId.isEmpty()) {
                continue;
            }
            Job job = jobMap.get(normalizeKey(application.getJobId()));
            assignmentsByTa.computeIfAbsent(studentId, ignored -> new ArrayList<>())
                    .add(new WorkloadAssignment(application, job));
        }

        Map<String, WorkloadInfo> workloadMap = new HashMap<>();
        for (Map.Entry<String, List<WorkloadAssignment>> entry : assignmentsByTa.entrySet()) {
            workloadMap.put(entry.getKey(), buildWorkloadInfo(entry.getValue()));
        }
        return workloadMap;
    }

    private WorkloadInfo buildWorkloadInfo(List<WorkloadAssignment> assignments) {
        WorkloadInfo info = new WorkloadInfo();
        if (assignments == null || assignments.isEmpty()) {
            return info;
        }

        info.applicationCount = assignments.size();
        for (WorkloadAssignment assignment : assignments) {
            info.totalHours += assignment.hours;
        }

        for (int i = 0; i < assignments.size(); i++) {
            for (int j = i + 1; j < assignments.size(); j++) {
                WorkloadAssignment first = assignments.get(i);
                WorkloadAssignment second = assignments.get(j);
                if (hasScheduleConflict(first, second)) {
                    info.conflictJobIds.add(normalizeKey(first.jobId));
                    info.conflictJobIds.add(normalizeKey(second.jobId));
                }
            }
        }
        return info;
    }

    private List<String> buildWorkloadWarningReasons(WorkloadInfo workloadInfo, String currentJobId) {
        List<String> reasons = new ArrayList<>();
        if (workloadInfo == null) {
            return reasons;
        }
        if (workloadInfo.conflictJobIds.contains(normalizeKey(currentJobId))) {
            reasons.add("Time conflict");
        }
        if (workloadInfo.totalHours > maxWeeklyWorkloadHours) {
            reasons.add("Hours limit exceeded");
        }
        return reasons;
    }

    private static String buildWorkloadLabel(WorkloadInfo workloadInfo) {
        int applicationCount = workloadInfo == null ? 0 : workloadInfo.applicationCount;
        double hours = workloadInfo == null ? 0 : workloadInfo.totalHours;
        String applicationLabel = applicationCount
                + (applicationCount == 1 ? " active application" : " active applications");
        return applicationLabel + ", " + formatHours(hours) + " assigned hours";
    }

    private static boolean hasScheduleConflict(WorkloadAssignment first, WorkloadAssignment second) {
        if (first == null || second == null || first.scheduleSlots.isEmpty() || second.scheduleSlots.isEmpty()) {
            return false;
        }
        for (ScheduleSlot firstSlot : first.scheduleSlots) {
            for (ScheduleSlot secondSlot : second.scheduleSlots) {
                if (firstSlot.overlaps(secondSlot)) {
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
        Matcher matcher = DAY_PATTERN.matcher(valueOrEmpty(schedule).toLowerCase(Locale.ROOT));
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
        Matcher matcher = TIME_RANGE_PATTERN.matcher(valueOrEmpty(schedule).toLowerCase(Locale.ROOT));
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
        int normalizedHour = hour;
        if ("am".equals(suffix)) {
            normalizedHour = hour == 12 ? 0 : hour;
        } else if ("pm".equals(suffix)) {
            normalizedHour = hour == 12 ? 12 : hour + 12;
        } else {
            return -1;
        }
        return normalizedHour * 60 + minute;
    }

    private static double parseHours(String rawHours) {
        Matcher matcher = HOURS_PATTERN.matcher(valueOrEmpty(rawHours));
        double maxHours = 0;
        while (matcher.find()) {
            try {
                maxHours = Math.max(maxHours, Double.parseDouble(matcher.group(1)));
            } catch (NumberFormatException ignored) {
                // Ignore malformed numeric fragments.
            }
        }
        return maxHours;
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
        if (Math.rint(hours) == hours) {
            return String.valueOf((int) hours);
        }
        return String.format(Locale.ROOT, "%.1f", hours);
    }

    private void sortRows(List<ApplicantReviewRow> rows, String sortMode) {
        Comparator<ApplicantReviewRow> comparator;
        if ("status".equals(sortMode)) {
            comparator = Comparator
                    .comparingInt((ApplicantReviewRow row) -> statusRank(row.getStatus()))
                    .thenComparing(Comparator.comparingInt(ApplicantReviewRow::getMatchScore).reversed())
                    .thenComparing(row -> valueOrEmpty(row.getTaName()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(row -> valueOrEmpty(row.getTaStudentId()), String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = Comparator
                    .comparingInt(ApplicantReviewRow::getMatchScore).reversed()
                    .thenComparingInt(row -> statusRank(row.getStatus()))
                    .thenComparing(row -> valueOrEmpty(row.getTaName()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(row -> valueOrEmpty(row.getTaStudentId()), String.CASE_INSENSITIVE_ORDER);
        }
        rows.sort(comparator);
    }

    private static String normalizeSortMode(String sortMode) {
        if ("status".equalsIgnoreCase(sortMode)) {
            return "status";
        }
        return "match";
    }

    private static int statusRank(String status) {
        String normalized = normalizeKey(status);
        return switch (normalized) {
            case "submitted" -> 0;
            case "in_review" -> 1;
            case "shortlisted" -> 2;
            case "accepted" -> 3;
            case "rejected" -> 4;
            default -> 5;
        };
    }

    private static boolean isActiveStatus(String status) {
        String normalized = normalizeKey(status);
        return normalized.isEmpty() || !"rejected".equals(normalized);
    }

    private static void applySkillStatus(ApplicantReviewRow row, TA profile, int score) {
        if (profile == null || row.getSkills().isBlank()) {
            row.setCoreSkillStatusLabel("No core skills provided");
            row.setCoreSkillStatusClass("tag-neutral");
            return;
        }
        row.setCoreSkillStatusLabel("Core skills listed");
        row.setCoreSkillStatusClass("tag-neutral");
    }

    private static String buildCoreSkillsHtml(String rawSkills, List<String> requirementTokens) {
        List<String> skills = splitSkills(rawSkills);
        if (skills.isEmpty()) {
            return "<span class=\"muted\">Not provided</span>";
        }

        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        StringBuilder html = new StringBuilder();
        for (String skill : skills) {
            String normalizedSkill = normalizeKey(skill);
            boolean matched = matchesRequirement(normalizedSkill, requirementSet);
            html.append("<span class=\"skill-token");
            if (matched) {
                html.append(" skill-token-match");
            }
            html.append("\">")
                    .append(escapeHtml(skill))
                    .append("</span>");
        }
        return html.toString();
    }

    private static boolean matchesRequirement(String normalizedSkill, Set<String> requirementSet) {
        if (normalizedSkill.isEmpty() || requirementSet.isEmpty()) {
            return false;
        }
        if (requirementSet.contains(normalizedSkill)) {
            return true;
        }
        List<String> parts = tokenize(normalizedSkill);
        for (String part : parts) {
            if (requirementSet.contains(part)) {
                return true;
            }
        }
        return false;
    }

    private static int calculateMatchScore(List<String> requirementTokens, List<String> skillTokens) {
        if (requirementTokens.isEmpty() || skillTokens.isEmpty()) {
            return 0;
        }
        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        int matched = 0;
        for (String requirement : requirementSet) {
            if (skillSet.contains(requirement)) {
                matched++;
            }
        }
        return (int) Math.round((matched * 100.0) / requirementSet.size());
    }

    private static List<String> intersection(List<String> requirementTokens, List<String> skillTokens) {
        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        List<String> result = new ArrayList<>();
        for (String skill : skillSet) {
            if (requirementSet.contains(skill)) {
                result.add(skill);
            }
        }
        return result;
    }

    private static List<String> difference(List<String> requirementTokens, List<String> skillTokens) {
        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        List<String> result = new ArrayList<>();
        for (String requirement : requirementSet) {
            if (!skillSet.contains(requirement)) {
                result.add(requirement);
            }
        }
        return result;
    }

    private static List<String> tokenize(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(raw.toLowerCase(Locale.ROOT).split("[^a-z0-9+]+"))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(token -> "c++".equals(token) ? "c" : token)
                .filter(token -> token.length() > 1 || "c".equals(token))
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toList());
    }

    private static List<String> splitSkills(String rawSkills) {
        if (rawSkills == null || rawSkills.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(rawSkills.split("[,;\\n]"))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
    }

    private static String joinTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "";
        }
        return String.join(", ", tokens);
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        return valueOrEmpty(second);
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        String result = value.replace("&", "&amp;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        return result.replace("'", "&#39;");
    }

    private static class WorkloadInfo {
        private int applicationCount;
        private double totalHours;
        private final Set<String> conflictJobIds = new HashSet<>();
    }

    private static class WorkloadAssignment {
        private final String jobId;
        private final double hours;
        private final List<ScheduleSlot> scheduleSlots;

        private WorkloadAssignment(Application application, Job job) {
            this.jobId = valueOrEmpty(application == null ? null : application.getJobId());
            this.hours = parseHours(job == null ? null : job.getHours());
            this.scheduleSlots = parseScheduleSlots(job == null ? null : job.getSchedule());
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
