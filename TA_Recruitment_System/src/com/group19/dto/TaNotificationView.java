package com.group19.dto;

public class TaNotificationView {
    private String message;
    private String createdAtDisplay;
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

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
