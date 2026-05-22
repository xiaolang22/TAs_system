package com.group19.dto;

public class MoNotificationView {
    private String message;
    private String createdAtDisplay;
    private String actionUrl;
    private boolean unread;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAtDisplay() {
        return createdAtDisplay;
    }

    public void setCreatedAtDisplay(String createdAtDisplay) {
        this.createdAtDisplay = createdAtDisplay;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
