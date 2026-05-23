package com.group19.service;

import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dto.MoNotificationView;
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
import java.util.UUID;

public class MoNewApplicationNotificationService {
    public static final String TYPE_NEW_APPLICATION = "NEW_APPLICATION_SUBMITTED";
    private static final int DASHBOARD_LIMIT = 10;
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final NotificationDao notificationDao;
    private final JobDao jobDao;

    public MoNewApplicationNotificationService(NotificationDao notificationDao, JobDao jobDao) {
        this.notificationDao = notificationDao;
        this.jobDao = jobDao;
    }

    public void notifyNewApplication(Application application) {
        if (application == null) {
            return;
        }

        Job job = jobDao.findById(application.getJobId());
        if (job == null || job.getOwnerMoUserId() == null || job.getOwnerMoUserId().isBlank()) {
            return;
        }

        String jobTitle = resolveJobTitle(application.getJobId());
        String taName = application.getTaName() == null || application.getTaName().isBlank()
                ? application.getTaStudentId()
                : application.getTaName().trim();
        String message = "TA "
                + taName
                + " 提交了岗位“"
                + jobTitle
                + "”的新申请。";

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setRecipientUserId(job.getOwnerMoUserId().trim());
        notification.setType(TYPE_NEW_APPLICATION);
        notification.setMessage(message);
        notification.setApplicationId(application.getApplicationId());
        notification.setJobId(application.getJobId());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now().toString());
        notificationDao.save(notification);
    }

    public List<MoNotificationView> loadForMoDashboard(String moUserId, String contextPath) {
        if (moUserId == null || moUserId.isBlank()) {
            return new ArrayList<>();
        }

        String basePath = contextPath == null ? "" : contextPath;
        List<Notification> notifications = new ArrayList<>(notificationDao.findByRecipientUserId(moUserId));
        notifications.removeIf(notification ->
                !TYPE_NEW_APPLICATION.equals(notification.getType())
                        || notification.isRead()
                        || !isMoOwnedJob(notification.getJobId(), moUserId));
        notifications.sort(Comparator
                .comparing((Notification n) -> parseDateTime(n.getCreatedAt()))
                .reversed());

        List<MoNotificationView> views = new ArrayList<>();
        int limit = Math.min(DASHBOARD_LIMIT, notifications.size());
        for (int i = 0; i < limit; i++) {
            Notification notification = notifications.get(i);
            MoNotificationView view = new MoNotificationView();
            view.setMessage(HtmlEscape.escape(notification.getMessage()));
            view.setCreatedAtDisplay(formatDisplay(notification.getCreatedAt()));
            view.setUnread(true);
            String jobId = notification.getJobId();
            String applicationId = notification.getApplicationId();
            if (jobId != null && !jobId.isBlank() && applicationId != null && !applicationId.isBlank()) {
                view.setActionUrl(basePath
                        + "/mo/applications?jobId="
                        + jobId
                        + "&applicationId="
                        + applicationId);
            } else if (jobId != null && !jobId.isBlank()) {
                view.setActionUrl(basePath + "/mo/applications?jobId=" + jobId);
            } else {
                view.setActionUrl(basePath + "/mo/jobs");
            }
            views.add(view);
        }
        return views;
    }

    public void markApplicationAsViewed(String moUserId, String applicationId) {
        if (moUserId == null || moUserId.isBlank() || applicationId == null || applicationId.isBlank()) {
            return;
        }
        notificationDao.markAsReadByRecipientAndApplication(moUserId, applicationId, TYPE_NEW_APPLICATION);
    }

    public int countUnread(String moUserId) {
        if (moUserId == null || moUserId.isBlank()) {
            return 0;
        }
        int count = 0;
        for (Notification notification : notificationDao.findByRecipientUserId(moUserId)) {
            if (!notification.isRead()
                    && TYPE_NEW_APPLICATION.equals(notification.getType())
                    && isMoOwnedJob(notification.getJobId(), moUserId)) {
                count++;
            }
        }
        return count;
    }

    private String resolveJobTitle(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return "未知岗位";
        }
        Job job = jobDao.findById(jobId);
        if (job == null || job.getTitle() == null || job.getTitle().isBlank()) {
            return "未知岗位";
        }
        return job.getTitle().trim();
    }

    private boolean isMoOwnedJob(String jobId, String moUserId) {
        if (jobId == null || jobId.isBlank() || moUserId == null || moUserId.isBlank()) {
            return false;
        }
        Job job = jobDao.findById(jobId);
        if (job == null || job.getOwnerMoUserId() == null) {
            return false;
        }
        return moUserId.trim().equalsIgnoreCase(job.getOwnerMoUserId().trim());
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
