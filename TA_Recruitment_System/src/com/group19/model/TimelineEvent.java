package com.group19.model;

/**
 * Timeline event entity class, recording status change events during the
 * application process. Each event is associated with an application and contains
 * the stage name, occurrence time, and notes.
 *
 * @author Group19
 * @since 1.0
 */
public class TimelineEvent {

    /** Unique identifier for the event */
    private String eventId;

    /** Associated application ID */
    private String applicationId;

    /** Process stage name (e.g. "submitted" / "under_review" / "approved") */
    private String stage;

    /** Event occurrence time */
    private String occurredAt;

    /** Event notes */
    private String note;

    /**
     * Default no-argument constructor.
     */
    public TimelineEvent() {
    }

    /** @return Unique identifier for the event */
    public String getEventId() { return eventId; }
    /** @param eventId Unique identifier for the event */
    public void setEventId(String eventId) { this.eventId = eventId; }

    /** @return Associated application ID */
    public String getApplicationId() { return applicationId; }
    /** @param applicationId Associated application ID */
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    /** @return Process stage name */
    public String getStage() { return stage; }
    /** @param stage Process stage name */
    public void setStage(String stage) { this.stage = stage; }

    /** @return Event occurrence time */
    public String getOccurredAt() { return occurredAt; }
    /** @param occurredAt Event occurrence time */
    public void setOccurredAt(String occurredAt) { this.occurredAt = occurredAt; }

    /** @return Event notes */
    public String getNote() { return note; }
    /** @param note Event notes */
    public void setNote(String note) { this.note = note; }
}
