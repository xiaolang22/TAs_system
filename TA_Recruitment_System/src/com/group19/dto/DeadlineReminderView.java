package com.group19.dto;

public class DeadlineReminderView {
    private String jobTitle;
    private String deadlineDisplay;
    private String daysLabel;
    private String reminderClass;
    private String actionUrl;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDeadlineDisplay() {
        return deadlineDisplay;
    }

    public void setDeadlineDisplay(String deadlineDisplay) {
        this.deadlineDisplay = deadlineDisplay;
    }

    public String getDaysLabel() {
        return daysLabel;
    }

    public void setDaysLabel(String daysLabel) {
        this.daysLabel = daysLabel;
    }

    public String getReminderClass() {
        return reminderClass;
    }

    public void setReminderClass(String reminderClass) {
        this.reminderClass = reminderClass;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
