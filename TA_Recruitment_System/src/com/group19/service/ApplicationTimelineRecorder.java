package com.group19.service;

import com.group19.dao.TimelineDao;
import com.group19.model.TimelineEvent;

import java.util.Locale;
import java.util.UUID;

/**
 * Application timeline recorder responsible for capturing status-change events
 * throughout the application workflow. Each status change (submission, in review,
 * accepted, rejected, etc.) creates a timeline event that is persisted.
 *
 * @author Group19
 * @since 1.0
 */
public class ApplicationTimelineRecorder {
    private final TimelineDao timelineDao;

    public ApplicationTimelineRecorder(TimelineDao timelineDao) {
        this.timelineDao = timelineDao;
    }

    public void recordSubmitted(String applicationId, String occurredAt) {
        append(applicationId, "SUBMITTED", occurredAt, "");
    }

    public void recordStatusChange(String applicationId, String newStatus, String occurredAt, String note) {
        String normalized = normalize(newStatus);
        if (normalized.isEmpty()) {
            return;
        }
        append(applicationId, normalized, occurredAt, note == null ? "" : note.trim());
    }

    private void append(String applicationId, String stage, String occurredAt, String note) {
        if (applicationId == null || applicationId.isBlank()) {
            return;
        }
        TimelineEvent event = new TimelineEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setApplicationId(applicationId.trim());
        event.setStage(stage);
        event.setOccurredAt(occurredAt == null ? "" : occurredAt.trim());
        event.setNote(note);
        timelineDao.append(event);
    }

    private static String normalize(String status) {
        if (status == null || status.isBlank()) {
            return "";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }
}
