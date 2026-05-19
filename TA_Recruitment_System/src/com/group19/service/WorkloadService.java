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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WorkloadService {
    private static final Pattern FIRST_NUMBER = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private final TADao taDao;
    private final ApplicationDao applicationDao;
    private final JobDao jobDao;

    public WorkloadService(TADao taDao, ApplicationDao applicationDao, JobDao jobDao) {
        this.taDao = taDao;
        this.applicationDao = applicationDao;
        this.jobDao = jobDao;
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
            position.setParsedHours(0);
            position.setHoursCounted(false);
            return position;
        }

        position.setTitle(firstNonBlank(job.getTitle(), "Untitled position"));
        position.setCategory(safe(job.getCategory()));
        position.setHoursText(safe(job.getHours()));

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
}
