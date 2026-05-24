package com.group19.model;

/**
 * Notification entity class, recording notification messages sent by the system
 * to users. Supports read/unread status marking and is associated with a specific
 * application or job position.
 *
 * @author Group19
 * @since 1.0
 */
public class Notification {

    /** Unique identifier for the notification */
    private String notificationId;

    /** User ID of the notification recipient */
    private String recipientUserId;

    /** Notification type (e.g. "status_update" / "new_application") */
    private String type;

    /** Notification message content */
    private String message;

    /** Associated application ID (nullable) */
    private String applicationId;

    /** Associated job ID (nullable) */
    private String jobId;

    /** Whether the notification has been read */
    private boolean read;

    /** Notification creation time */
    private String createdAt;

    /**
     * Default no-argument constructor.
     */
    public Notification() {
    }

    /** @return Unique identifier for the notification */
    public String getNotificationId() { return notificationId; }
    /** @param notificationId Unique identifier for the notification */
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    /** @return Recipient user ID */
    public String getRecipientUserId() { return recipientUserId; }
    /** @param recipientUserId Recipient user ID */
    public void setRecipientUserId(String recipientUserId) { this.recipientUserId = recipientUserId; }

    /** @return Notification type */
    public String getType() { return type; }
    /** @param type Notification type */
    public void setType(String type) { this.type = type; }

    /** @return Notification message content */
    public String getMessage() { return message; }
    /** @param message Notification message content */
    public void setMessage(String message) { this.message = message; }

    /** @return Associated application ID */
    public String getApplicationId() { return applicationId; }
    /** @param applicationId Associated application ID */
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    /** @return Associated job ID */
    public String getJobId() { return jobId; }
    /** @param jobId Associated job ID */
    public void setJobId(String jobId) { this.jobId = jobId; }

    /** @return Whether the notification has been read */
    public boolean isRead() { return read; }
    /** @param read Whether the notification has been read */
    public void setRead(boolean read) { this.read = read; }

    /** @return Creation time */
    public String getCreatedAt() { return createdAt; }
    /** @param createdAt Creation time */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
