package com.group19.dto;

/**
 * MO (Module Organiser) notification view data transfer object (DTO).
 * <p>
 * Used for displaying each notification in the MO notification centre. Contains
 * the notification message content, creation time display text, an action link
 * for navigation, and an unread status flag.
 * </p>
 *
 * @author Group 19
 */
public class MoNotificationView {

    /** Notification message content */
    private String message;

    /** Display text for the notification creation time */
    private String createdAtDisplay;

    /** Action link for navigation when the notification is clicked */
    private String actionUrl;

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

    /** @return action link for navigation */
    public String getActionUrl() {
        return actionUrl;
    }

    /** @param actionUrl action link for navigation */
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
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
