package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dto.DeadlineReminderView;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.util.HtmlEscape;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DeadlineReminderService {
    public static final int DEFAULT_WITHIN_DAYS = 14;

    private final JobService jobService;
    private final ApplicationDao applicationDao;
    private final SavedJobService savedJobService;

    public DeadlineReminderService(
            JobService jobService,
            ApplicationDao applicationDao,
            SavedJobService savedJobService) {
        this.jobService = jobService;
        this.applicationDao = applicationDao;
        this.savedJobService = savedJobService;
    }

    public List<DeadlineReminderView> buildRemindersForTa(String taStudentId, LocalDate today, String contextPath) {
        Set<String> relevantJobIds = collectTaRelevantJobIds(taStudentId);
        List<Job> upcoming = filterJobsByIds(jobService.findUpcomingDeadlineJobs(today, DEFAULT_WITHIN_DAYS), relevantJobIds);
        return buildReminderViews(upcoming, today, contextPath, true);
    }

    public List<DeadlineReminderView> buildRemindersForMo(LocalDate today, String contextPath) {
        return buildReminderViews(jobService.findUpcomingDeadlineJobs(today, DEFAULT_WITHIN_DAYS), today, contextPath, false);
    }

    private Set<String> collectTaRelevantJobIds(String taStudentId) {
        Set<String> jobIds = new LinkedHashSet<>();
        if (taStudentId == null || taStudentId.isBlank()) {
            return jobIds;
        }
        for (Application application : applicationDao.findByTaStudentId(taStudentId)) {
            if (application.getJobId() != null && !application.getJobId().isBlank()) {
                jobIds.add(application.getJobId());
            }
        }
        jobIds.addAll(savedJobService.findSavedJobIds(taStudentId));
        return jobIds;
    }

    private static List<Job> filterJobsByIds(List<Job> jobs, Set<String> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Job> filtered = new ArrayList<>();
        for (Job job : jobs) {
            if (job != null && job.getJobId() != null && jobIds.contains(job.getJobId())) {
                filtered.add(job);
            }
        }
        return filtered;
    }

    private List<DeadlineReminderView> buildReminderViews(
            List<Job> jobs,
            LocalDate today,
            String contextPath,
            boolean taLinks) {
        List<DeadlineReminderView> reminders = new ArrayList<>();
        if (jobs == null || jobs.isEmpty()) {
            return reminders;
        }

        String basePath = contextPath == null ? "" : contextPath;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (Job job : jobs) {
            LocalDate deadline = JobService.parseDeadlineDate(job.getDeadline());
            if (deadline == null) {
                continue;
            }

            long daysUntil = ChronoUnit.DAYS.between(today, deadline);
            DeadlineReminderView view = new DeadlineReminderView();
            String title = job.getTitle() == null || job.getTitle().isBlank() ? "Untitled position" : job.getTitle().trim();
            view.setJobTitle(HtmlEscape.escape(title));
            view.setDeadlineDisplay(deadline.format(dateFormatter));
            view.setDaysLabel(buildDaysLabel(daysUntil));
            view.setReminderClass(reminderClass(daysUntil));
            if (taLinks) {
                view.setActionUrl(basePath + "/jobs?jobId=" + job.getJobId());
            } else {
                view.setActionUrl(basePath + "/mo/applications?jobId=" + job.getJobId());
            }
            reminders.add(view);
        }
        return reminders;
    }

    private static String buildDaysLabel(long daysUntil) {
        if (daysUntil <= 0) {
            return "Deadline is today";
        }
        if (daysUntil == 1) {
            return "Deadline in 1 day";
        }
        return "Deadline in " + daysUntil + " days";
    }

    private static String reminderClass(long daysUntil) {
        if (daysUntil <= 0) {
            return "tag-alert";
        }
        if (daysUntil <= 3) {
            return "tag-warning";
        }
        return "tag-neutral";
    }
}
