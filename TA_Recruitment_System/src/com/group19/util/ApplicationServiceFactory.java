package com.group19.util;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.TimelineDao;
import com.group19.service.ApplicationService;
import com.group19.service.ApplicationTimelineRecorder;
import com.group19.service.MoNewApplicationNotificationService;
import com.group19.service.TaStatusNotificationService;
import jakarta.servlet.ServletContext;

import java.nio.file.Path;

public final class ApplicationServiceFactory {
    private ApplicationServiceFactory() {
    }

    public static ApplicationService create(ServletContext context) {
        Path appFilePath = DataPathResolver.resolve(
                context, "applicationDataFile", "/data/applications.json", "applications.json");
        Path timelineFilePath = DataPathResolver.resolve(
                context, "timelineDataFile", "/data/timelines.json", "timelines.json");
        Path jobFilePath = DataPathResolver.resolve(
                context, "jobDataFile", "/data/jobs.json", "jobs.json");
        Path notificationFilePath = DataPathResolver.resolve(
                context, "notificationDataFile", "/data/notifications.json", "notifications.json");

        ApplicationDao applicationDao = new ApplicationDao(appFilePath);
        ApplicationTimelineRecorder timelineRecorder =
                new ApplicationTimelineRecorder(new TimelineDao(timelineFilePath));
        JobDao jobDao = new JobDao(jobFilePath);
        NotificationDao notificationDao = new NotificationDao(notificationFilePath);

        TaStatusNotificationService taStatusNotificationService =
                new TaStatusNotificationService(notificationDao, jobDao);
        MoNewApplicationNotificationService moNewApplicationNotificationService =
                new MoNewApplicationNotificationService(notificationDao, jobDao);

        return new ApplicationService(
                applicationDao,
                timelineRecorder,
                taStatusNotificationService,
                moNewApplicationNotificationService);
    }
}
