package com.group19.dto;

/**
 * TA (Teaching Assistant) notification view data transfer object (DTO).
 * <p>
 * Used for displaying each notification in the TA notification centre.
 * Contains the notification message content, creation time display text,
 * and an unread status flag.
 * </p>
 *
 * @author Group 19
 */
public class TaNotificationView {

    /** Notification message content */
    private String message;

    /** Display text for the notification creation time */
    private String createdAtDisplay;

    /** Whether the notification is unread */
    private boolean unread;

    /** @return notification message content */
    public String getMessage() {
        return message;
    }

    /** @param message notification message content */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return display text for the notification creation time */
    public String getCreatedAtDisplay() {
        return createdAtDisplay;
    }

    /** @param createdAtDisplay display text for the notification creation time */
    public void setCreatedAtDisplay(String createdAtDisplay) {
        this.createdAtDisplay = createdAtDisplay;
    }

    /** @return whether the notification is unread */
    public boolean isUnread() {
        return unread;
    }

    /** @param unread whether the notification is unread */
    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
