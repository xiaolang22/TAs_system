package com.group19.service;

import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dto.TaNotificationView;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.Notification;
import com.group19.util.HtmlEscape;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TaStatusNotificationService {
    public static final String TYPE_STATUS_CHANGED = "APPLICATION_STATUS_CHANGED";
    private static final int DASHBOARD_LIMIT = 10;
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", Locale.ENGLISH);

    private final NotificationDao notificationDao;
    private final JobDao jobDao;

    public TaStatusNotificationService(NotificationDao notificationDao, JobDao jobDao) {
        this.notificationDao = notificationDao;
        this.jobDao = jobDao;
    }

    public void notifyStatusChanged(Application application, String previousStatus, String newStatus) {
        if (application == null || newStatus == null || newStatus.isBlank()) {
            return;
        }
        String previous = previousStatus == null ? "" : previousStatus.trim().toUpperCase(Locale.ROOT);
        String current = newStatus.trim().toUpperCase(Locale.ROOT);
        if (current.equals(previous)) {
            return;
        }

        String taStudentId = application.getTaStudentId();
        if (taStudentId == null || taStudentId.isBlank()) {
            return;
        }

        String jobTitle = resolveJobTitle(application.getJobId());
        String displayStatus = TaApplicationStatusService.toDisplayStatus(current);
        String message = "Your application for \""
                + jobTitle
                + "\" was updated to "
                + displayStatus
                + ".";

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setRecipientUserId(taStudentId);
        notification.setType(TYPE_STATUS_CHANGED);
        notification.setMessage(message);
        notification.setApplicationId(application.getApplicationId());
        notification.setJobId(application.getJobId());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now().toString());
        notificationDao.save(notification);
    }

    public List<TaNotificationView> loadForTaDashboard(String taStudentId) {
        if (taStudentId == null || taStudentId.isBlank()) {
            return new ArrayList<>();
        }

        List<Notification> notifications = new ArrayList<>(notificationDao.findByRecipientUserId(taStudentId));
        notifications.removeIf(notification -> !TYPE_STATUS_CHANGED.equals(notification.getType()));
        notifications.sort(Comparator
                .comparing((Notification n) -> parseDateTime(n.getCreatedAt()))
                .reversed());

        List<TaNotificationView> views = new ArrayList<>();
        int limit = Math.min(DASHBOARD_LIMIT, notifications.size());
        for (int i = 0; i < limit; i++) {
            Notification notification = notifications.get(i);
            TaNotificationView view = new TaNotificationView();
            view.setMessage(HtmlEscape.escape(notification.getMessage()));
            view.setCreatedAtDisplay(formatDisplay(notification.getCreatedAt()));
            view.setUnread(!notification.isRead());
            views.add(view);
        }
        return views;
    }

    public int countUnread(String taStudentId) {
        if (taStudentId == null || taStudentId.isBlank()) {
            return 0;
        }
        int count = 0;
        for (Notification notification : notificationDao.findByRecipientUserId(taStudentId)) {
            if (!notification.isRead() && TYPE_STATUS_CHANGED.equals(notification.getType())) {
                count++;
            }
        }
        return count;
    }

    private String resolveJobTitle(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return "Unknown position";
        }
        Job job = jobDao.findById(jobId);
        if (job == null || job.getTitle() == null || job.getTitle().isBlank()) {
            return "Unknown position";
        }
        return job.getTitle().trim();
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
}
