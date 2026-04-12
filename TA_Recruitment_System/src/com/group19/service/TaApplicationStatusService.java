package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TimelineDao;
import com.group19.dto.TaApplicationOverview;
import com.group19.dto.TaTimelineStep;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.TimelineEvent;
import com.group19.util.HtmlEscape;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TaApplicationStatusService {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", Locale.ENGLISH);

    private final ApplicationDao applicationDao;
    private final JobDao jobDao;
    private final TimelineDao timelineDao;

    public TaApplicationStatusService(ApplicationDao applicationDao, JobDao jobDao, TimelineDao timelineDao) {
        this.applicationDao = applicationDao;
        this.jobDao = jobDao;
        this.timelineDao = timelineDao;
    }

    public List<TaApplicationOverview> loadOverviewsForTa(String taStudentId) {
        if (taStudentId == null || taStudentId.isBlank()) {
            return new ArrayList<>();
        }
        List<Application> applications = new ArrayList<>(applicationDao.findByTaStudentId(taStudentId));
        applications.sort(Comparator.comparing((Application app) -> parseDateTime(app.getUpdatedAt())).reversed());

        List<TaApplicationOverview> result = new ArrayList<>();
        for (Application application : applications) {
            result.add(buildOverview(application));
        }
        return result;
    }

    private TaApplicationOverview buildOverview(Application application) {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setApplicationId(safe(application.getApplicationId()));
        overview.setJobId(safe(application.getJobId()));

        Job job = jobDao.findById(application.getJobId());
        String rawTitle = job == null || job.getTitle() == null || job.getTitle().isBlank()
                ? "Unknown position"
                : job.getTitle().trim();
        overview.setJobTitle(HtmlEscape.escape(rawTitle));

        String normalizedStatus = normalizeStatusCode(application.getStatus());
        overview.setStatusLabel(toDisplayStatus(normalizedStatus));
        overview.setStatusPillClass(statusPillClass(normalizedStatus));
        overview.setLastUpdatedDisplay(formatDisplay(application.getUpdatedAt()));

        List<TimelineEvent> stored = timelineDao.findByApplicationIdOrdered(application.getApplicationId());
        List<TaTimelineStep> steps;
        if (stored.isEmpty()) {
            steps = synthesizeSteps(application, normalizedStatus);
        } else {
            steps = new ArrayList<>();
            if (!startsWithSubmitted(stored)) {
                TaTimelineStep submitted = new TaTimelineStep();
                submitted.setTitle(toDisplayStatus("SUBMITTED"));
                submitted.setOccurredAtDisplay(formatDisplay(application.getSubmittedAt()));
                submitted.setPillClass(statusPillClass("SUBMITTED"));
                submitted.setDetail("");
                steps.add(submitted);
            }
            steps.addAll(mapStoredEvents(stored));
        }
        overview.setTimelineSteps(steps);
        return overview;
    }

    private static boolean startsWithSubmitted(List<TimelineEvent> events) {
        if (events == null || events.isEmpty()) {
            return false;
        }
        return "SUBMITTED".equals(TimelineDao.normalizeStage(events.get(0).getStage()));
    }

    private List<TaTimelineStep> mapStoredEvents(List<TimelineEvent> events) {
        List<TaTimelineStep> steps = new ArrayList<>();
        for (TimelineEvent event : events) {
            String stage = TimelineDao.normalizeStage(event.getStage());
            TaTimelineStep step = new TaTimelineStep();
            step.setTitle(toDisplayStatus(stage));
            step.setOccurredAtDisplay(formatDisplay(event.getOccurredAt()));
            step.setPillClass(statusPillClass(stage));
            step.setDetail(buildDetailLine(event.getNote()));
            steps.add(step);
        }
        return steps;
    }

    private List<TaTimelineStep> synthesizeSteps(Application application, String normalizedStatus) {
        List<TaTimelineStep> steps = new ArrayList<>();

        TaTimelineStep submitted = new TaTimelineStep();
        submitted.setTitle(toDisplayStatus("SUBMITTED"));
        submitted.setOccurredAtDisplay(formatDisplay(application.getSubmittedAt()));
        submitted.setPillClass(statusPillClass("SUBMITTED"));
        submitted.setDetail("");
        steps.add(submitted);

        if (!"SUBMITTED".equals(normalizedStatus)) {
            TaTimelineStep current = new TaTimelineStep();
            current.setTitle(toDisplayStatus(normalizedStatus));
            current.setOccurredAtDisplay(formatDisplay(application.getUpdatedAt()));
            current.setPillClass(statusPillClass(normalizedStatus));
            current.setDetail(buildDetailLine(application.getDecisionNote()));
            steps.add(current);
        }

        return steps;
    }

    private static String buildDetailLine(String note) {
        if (note == null) {
            return "";
        }
        String trimmed = note.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        if (trimmed.length() > 220) {
            return HtmlEscape.escape(trimmed.substring(0, 217) + "...");
        }
        return HtmlEscape.escape(trimmed);
    }

    public static String toDisplayStatus(String normalizedStage) {
        return switch (normalizedStage) {
            case "SUBMITTED" -> "Submitted";
            case "IN_REVIEW" -> "In Review";
            case "SHORTLISTED" -> "Shortlisted";
            case "ACCEPTED" -> "Accepted";
            case "REJECTED" -> "Rejected";
            default -> "Updated";
        };
    }

    public static String normalizeStatusCode(String status) {
        if (status == null || status.isBlank()) {
            return "SUBMITTED";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private static String statusPillClass(String normalizedStatus) {
        return switch (normalizedStatus) {
            case "SHORTLISTED" -> "status-pill tag-warning";
            case "ACCEPTED" -> "status-pill tag-good";
            case "REJECTED" -> "status-pill tag-alert";
            case "IN_REVIEW" -> "status-pill tag-neutral";
            default -> "status-pill tag-neutral";
        };
    }

    private static String formatDisplay(String iso) {
        if (iso == null || iso.isBlank()) {
            return "";
        }
        try {
            return LocalDateTime.parse(iso.trim()).format(DISPLAY_FORMAT);
        } catch (DateTimeParseException ex) {
            return iso.trim();
        }
    }

    private static LocalDateTime parseDateTime(String iso) {
        if (iso == null || iso.isBlank()) {
            return LocalDateTime.MIN;
        }
        try {
            return LocalDateTime.parse(iso.trim());
        } catch (DateTimeParseException ex) {
            return LocalDateTime.MIN;
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
