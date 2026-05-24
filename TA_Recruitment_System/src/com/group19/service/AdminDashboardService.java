package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.AdminDashboardData;
import com.group19.dto.AdminFeedItem;
import com.group19.dto.ServiceResult;
import com.group19.dto.TaWorkloadRow;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.UserAccount;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Admin dashboard service responsible for aggregating system-wide overview data.
 * Covers TA/MO/job statistics, application status distribution, workload monitoring,
 * and recent activity summaries.
 *
 * @author Group19
 * @since 1.0
 */
public class AdminDashboardService {
    private final UserAccountDao userAccountDao;
    private final ApplicationDao applicationDao;
    private final JobDao jobDao;
    private final JobService jobService;
    private final WorkloadService workloadService;

    public AdminDashboardService(UserAccountDao userAccountDao,
                                 ApplicationDao applicationDao,
                                 JobDao jobDao,
                                 TADao taDao) {
        this.userAccountDao = userAccountDao;
        this.applicationDao = applicationDao;
        this.jobDao = jobDao;
        this.jobService = new JobService(jobDao);
        this.workloadService = new WorkloadService(taDao, applicationDao, jobDao);
    }

    public ServiceResult<AdminDashboardData> loadDashboard(String selectedTaId,
                                                           String selectedMoId,
                                                           String selectedJobId) {
        try {
            List<UserAccount> allAccounts = userAccountDao.findAll();
            List<UserAccount> taAccounts = filterByRole(allAccounts, "TA");
            List<UserAccount> moAccounts = filterByRole(allAccounts, "MO");
            List<Job> jobs = new ArrayList<>(jobDao.findAll());
            List<Application> applications = applicationDao.findAll();
            List<TaWorkloadRow> workloadRows = loadWorkloadRows();

            AdminDashboardData data = new AdminDashboardData();
            data.setTaAccounts(sortAccounts(taAccounts));
            data.setMoAccounts(sortAccounts(moAccounts));
            data.setJobs(sortJobs(jobs));
            data.setSelectedTa(findAccountByUserId(taAccounts, selectedTaId));
            data.setSelectedMo(findAccountByUserId(moAccounts, selectedMoId));
            data.setSelectedJob(findJobById(jobs, selectedJobId));

            LocalDate today = LocalDate.now();
            int openJobCount = jobService.findOpenActiveJobs(today).size();
            data.setOpenJobCount(openJobCount);
            data.setClosedJobCount(Math.max(0, jobs.size() - openJobCount));
            data.setTaCount(taAccounts.size());
            data.setMoCount(moAccounts.size());
            data.setPendingApplicationCount(countPendingApplications(applications));
            data.setAcceptedApplicationCount(countApplicationsByStatus(applications, "accepted"));
            data.setRejectedApplicationCount(countApplicationsByStatus(applications, "rejected"));
            data.setWorkloadWarningCount((int) workloadRows.stream().filter(TaWorkloadRow::isHasWorkloadWarning).count());
            data.setTimeConflictCount((int) workloadRows.stream()
                    .filter(row -> row.getWorkloadWarningReasons().stream()
                            .anyMatch(reason -> "Time conflict".equalsIgnoreCase(reason)))
                    .count());
            data.setRecentJobs(buildRecentJobs(jobs));
            data.setRecentApplications(buildRecentApplications(applications, jobs));
            data.setRecentAlerts(buildRecentAlerts(workloadRows, allAccounts));
            return ServiceResult.success(data, "Admin dashboard data loaded successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to read admin dashboard data.");
        }
    }

    public ServiceResult<UserAccount> resetAccountPassword(String userId, String expectedRole) {
        if (isBlank(userId)) {
            return ServiceResult.failure("The account identifier is required.");
        }
        try {
            UserAccount account = userAccountDao.findByUserId(userId.trim());
            if (!isAllowedManagedRole(account, expectedRole)) {
                return ServiceResult.failure("The target account could not be found.");
            }
            account.setPassword(defaultPasswordForRole(account.getRole()));
            UserAccount saved = userAccountDao.updateByUserId(account);
            if (saved == null) {
                return ServiceResult.failure("Failed to reset the password.");
            }
            return ServiceResult.success(saved, "Password reset successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to reset the password.");
        }
    }

    public ServiceResult<UserAccount> toggleAccountFreeze(String userId, String expectedRole) {
        if (isBlank(userId)) {
            return ServiceResult.failure("The account identifier is required.");
        }
        try {
            UserAccount account = userAccountDao.findByUserId(userId.trim());
            if (!isAllowedManagedRole(account, expectedRole)) {
                return ServiceResult.failure("The target account could not be found.");
            }
            account.setFrozen(!account.isFrozen());
            UserAccount saved = userAccountDao.updateByUserId(account);
            if (saved == null) {
                return ServiceResult.failure("Failed to update the account status.");
            }
            return ServiceResult.success(saved, saved.isFrozen() ? "Account frozen." : "Account unfrozen.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to update the account status.");
        }
    }

    public ServiceResult<Job> updateJob(String jobId, String title, String description, String requirements,
                                        String hours, String schedule, String deadline, String status) {
        if (isBlank(jobId)) {
            return ServiceResult.failure("Job ID is required.");
        }
        Job existing = jobDao.findById(jobId.trim());
        if (existing == null) {
            return ServiceResult.failure("Job not found.");
        }
        Job updated = new Job();
        updated.setJobId(existing.getJobId());
        updated.setTitle(trimToEmpty(title));
        updated.setDescription(trimToEmpty(description));
        updated.setRequirements(trimToEmpty(requirements));
        updated.setHours(trimToEmpty(hours));
        updated.setSchedule(trimToEmpty(schedule));
        updated.setDeadline(trimToEmpty(deadline));
        updated.setOwnerMoUserId(existing.getOwnerMoUserId());
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setStatus(normalizeJobStatus(status, existing.getStatus()));
        return jobService.updateJobAsAdmin(updated);
    }

    public ServiceResult<Void> deleteJob(String jobId) {
        return jobService.deleteJobAsAdmin(jobId);
    }

    private List<TaWorkloadRow> loadWorkloadRows() {
        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows("", "all");
        return result.isSuccess() ? result.getData() : new ArrayList<>();
    }

    private static List<UserAccount> filterByRole(List<UserAccount> accounts, String role) {
        if (accounts == null) {
            return new ArrayList<>();
        }
        return accounts.stream()
                .filter(account -> account != null && role.equalsIgnoreCase(trimToEmpty(account.getRole())))
                .collect(Collectors.toList());
    }

    private static List<UserAccount> sortAccounts(List<UserAccount> accounts) {
        return accounts.stream()
                .sorted(Comparator
                        .comparing((UserAccount account) -> trimToEmpty(account.getDisplayName()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(account -> trimToEmpty(account.getUsername()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private static List<Job> sortJobs(List<Job> jobs) {
        return jobs.stream()
                .sorted(Comparator
                        .comparing((Job job) -> parseTimeForSort(job),
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(job -> trimToEmpty(job.getTitle()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private static UserAccount findAccountByUserId(List<UserAccount> accounts, String userId) {
        if (accounts == null || isBlank(userId)) {
            return null;
        }
        for (UserAccount account : accounts) {
            if (account != null && userId.trim().equalsIgnoreCase(trimToEmpty(account.getUserId()))) {
                return account;
            }
        }
        return null;
    }

    private static Job findJobById(List<Job> jobs, String jobId) {
        if (jobs == null || isBlank(jobId)) {
            return null;
        }
        for (Job job : jobs) {
            if (job != null && jobId.trim().equalsIgnoreCase(trimToEmpty(job.getJobId()))) {
                return job;
            }
        }
        return null;
    }

    private static int countPendingApplications(List<Application> applications) {
        int count = 0;
        for (Application application : applications) {
            String status = trimToEmpty(application == null ? null : application.getStatus()).toLowerCase(Locale.ROOT);
            if (!"accepted".equals(status) && !"rejected".equals(status)) {
                count++;
            }
        }
        return count;
    }

    private static int countApplicationsByStatus(List<Application> applications, String expectedStatus) {
        int count = 0;
        for (Application application : applications) {
            if (application != null && expectedStatus.equalsIgnoreCase(trimToEmpty(application.getStatus()))) {
                count++;
            }
        }
        return count;
    }

    private static List<AdminFeedItem> buildRecentJobs(List<Job> jobs) {
        return sortJobs(jobs).stream()
                .limit(5)
                .map(job -> new AdminFeedItem(
                        trimToEmpty(job.getTitle()),
                        "Job ID " + trimToEmpty(job.getJobId()) + " | " + buildJobStatusLabel(job)))
                .collect(Collectors.toList());
    }

    private static List<AdminFeedItem> buildRecentApplications(List<Application> applications, List<Job> jobs) {
        List<Application> sorted = applications.stream()
                .filter(application -> application != null)
                .sorted(Comparator
                        .comparing((Application application) -> parseTimeForSort(application.getSubmittedAt()),
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(application -> trimToEmpty(application.getTaName()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        return sorted.stream()
                .limit(5)
                .map(application -> new AdminFeedItem(
                        trimToEmpty(application.getTaName()),
                        resolveJobTitle(jobs, application.getJobId()) + " | "
                                + buildApplicationStatusLabel(application.getStatus())))
                .collect(Collectors.toList());
    }

    private static List<AdminFeedItem> buildRecentAlerts(List<TaWorkloadRow> workloadRows, List<UserAccount> accounts) {
        List<AdminFeedItem> items = new ArrayList<>();
        for (UserAccount account : accounts) {
            if (account != null && account.isFrozen()
                    && ("TA".equalsIgnoreCase(trimToEmpty(account.getRole()))
                    || "MO".equalsIgnoreCase(trimToEmpty(account.getRole())))) {
                items.add(new AdminFeedItem(
                        "Account frozen: " + trimToEmpty(account.getDisplayName()),
                        trimToEmpty(account.getRole()) + " | " + trimToEmpty(account.getUsername())));
            }
        }
        for (TaWorkloadRow row : workloadRows) {
            if (row == null || !row.isHasWorkloadWarning()) {
                continue;
            }
            String reason = row.getWorkloadWarningReasons().isEmpty()
                    ? "Workload warning"
                    : row.getWorkloadWarningReasons().get(0);
            items.add(new AdminFeedItem(
                    trimToEmpty(row.getName()),
                    reason + " | " + trimToEmpty(row.getTotalAssignedHoursLabel())));
        }
        return items.stream().limit(5).collect(Collectors.toList());
    }

    private static String resolveJobTitle(List<Job> jobs, String jobId) {
        Job job = findJobById(jobs, jobId);
        return job == null ? "Unknown job" : trimToEmpty(job.getTitle());
    }

    private static String buildJobStatusLabel(Job job) {
        if (job == null) {
            return "-";
        }
        String status = trimToEmpty(job.getStatus()).toUpperCase(Locale.ROOT);
        if ("CLOSED".equals(status)) {
            return "Closed";
        }
        LocalDate deadline = JobService.parseDeadlineDate(job.getDeadline());
        if (deadline != null && LocalDate.now().isAfter(deadline)) {
            return "Closed";
        }
        return "Open";
    }

    private static String buildApplicationStatusLabel(String rawStatus) {
        String status = trimToEmpty(rawStatus).toUpperCase(Locale.ROOT);
        switch (status) {
            case "SUBMITTED":
                return "Submitted";
            case "IN_REVIEW":
                return "In Review";
            case "SHORTLISTED":
                return "Shortlisted";
            case "ACCEPTED":
                return "Accepted";
            case "REJECTED":
                return "Rejected";
            default:
                return status.isEmpty() ? "-" : status;
        }
    }

    private static boolean isAllowedManagedRole(UserAccount account, String expectedRole) {
        return account != null
                && !isBlank(expectedRole)
                && expectedRole.equalsIgnoreCase(trimToEmpty(account.getRole()));
    }

    private static String defaultPasswordForRole(String role) {
        if ("MO".equalsIgnoreCase(trimToEmpty(role))) {
            return "mo123456";
        }
        return "ta123456";
    }

    private static String normalizeJobStatus(String status, String fallback) {
        String normalized = trimToEmpty(status).toUpperCase(Locale.ROOT);
        if ("CLOSED".equals(normalized)) {
            return "CLOSED";
        }
        if ("OPEN".equals(normalized)) {
            return "OPEN";
        }
        String fallbackNormalized = trimToEmpty(fallback).toUpperCase(Locale.ROOT);
        return "CLOSED".equals(fallbackNormalized) ? "CLOSED" : "OPEN";
    }

    private static LocalDateTime parseTimeForSort(Job job) {
        return parseTimeForSort(job == null ? null : job.getCreatedAt());
    }

    private static LocalDateTime parseTimeForSort(String raw) {
        if (isBlank(raw)) {
            return null;
        }
        try {
            return LocalDateTime.parse(raw.trim());
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
