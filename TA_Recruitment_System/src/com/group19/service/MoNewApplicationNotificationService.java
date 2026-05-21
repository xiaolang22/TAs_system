package com.group19.service;

import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.MoNotificationView;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.Notification;
import com.group19.model.UserAccount;
import com.group19.util.HtmlEscape;

import java.io.IOException;
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
            DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", java.util.Locale.ENGLISH);

    private final NotificationDao notificationDao;
    private final JobDao jobDao;
    private final UserAccountDao userAccountDao;

    public MoNewApplicationNotificationService(
            NotificationDao notificationDao,
            JobDao jobDao,
            UserAccountDao userAccountDao) {
        this.notificationDao = notificationDao;
        this.jobDao = jobDao;
        this.userAccountDao = userAccountDao;
    }

    public void notifyNewApplication(Application application) {
        if (application == null) {
            return;
        }

        String jobTitle = resolveJobTitle(application.getJobId());
        String taName = application.getTaName() == null || application.getTaName().isBlank()
                ? application.getTaStudentId()
                : application.getTaName().trim();
        String message = "New application from "
                + taName
                + " for \""
                + jobTitle
                + "\".";

        for (UserAccount officer : findModuleOfficers()) {
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setRecipientUserId(officer.getUserId());
            notification.setType(TYPE_NEW_APPLICATION);
            notification.setMessage(message);
            notification.setApplicationId(application.getApplicationId());
            notification.setJobId(application.getJobId());
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now().toString());
            notificationDao.save(notification);
        }
    }

    public List<MoNotificationView> loadForMoDashboard(String moUserId, String contextPath) {
        if (moUserId == null || moUserId.isBlank()) {
            return new ArrayList<>();
        }

        String basePath = contextPath == null ? "" : contextPath;
        List<Notification> notifications = new ArrayList<>(notificationDao.findByRecipientUserId(moUserId));
        notifications.removeIf(notification -> !TYPE_NEW_APPLICATION.equals(notification.getType()));
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
            view.setUnread(!notification.isRead());
            String jobId = notification.getJobId();
            if (jobId != null && !jobId.isBlank()) {
                view.setActionUrl(basePath + "/mo/applications?jobId=" + jobId);
            } else {
                view.setActionUrl(basePath + "/mo/jobs");
            }
            views.add(view);
        }
        return views;
    }

    public int countUnread(String moUserId) {
        if (moUserId == null || moUserId.isBlank()) {
            return 0;
        }
        int count = 0;
        for (Notification notification : notificationDao.findByRecipientUserId(moUserId)) {
            if (!notification.isRead() && TYPE_NEW_APPLICATION.equals(notification.getType())) {
                count++;
            }
        }
        return count;
    }

    private List<UserAccount> findModuleOfficers() {
        List<UserAccount> officers = new ArrayList<>();
        try {
            for (UserAccount account : userAccountDao.findAll()) {
                if (account.getRole() != null && "MO".equalsIgnoreCase(account.getRole().trim())) {
                    officers.add(account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return officers;
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
