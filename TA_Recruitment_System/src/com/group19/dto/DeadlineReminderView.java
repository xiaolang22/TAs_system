package com.group19.dto;

/**
 * Deadline reminder view data transfer object (DTO).
 * <p>
 * Used for displaying deadline reminders on the system homepage or notification panel.
 * Contains the job title, deadline display text, days-remaining label, reminder CSS style
 * class, and an action link for navigation.
 * </p>
 *
 * @author Group 19
 */
public class DeadlineReminderView {

    /** Job title */
    private String jobTitle;

    /** Deadline display text (e.g., "2026-06-15") */
    private String deadlineDisplay;

    /** Days-remaining label (e.g., "3 days left") */
    private String daysLabel;

    /** CSS style class for the reminder (e.g., "urgent", "warning") */
    private String reminderClass;

    /** Action link for navigation when the reminder is clicked */
    private String actionUrl;

    /** @return job title */
    public String getJobTitle() {
        return jobTitle;
    }

    /** @param jobTitle job title */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /** @return deadline display text */
    public String getDeadlineDisplay() {
        return deadlineDisplay;
    }

    /** @param deadlineDisplay deadline display text */
    public void setDeadlineDisplay(String deadlineDisplay) {
        this.deadlineDisplay = deadlineDisplay;
    }

    /** @return days-remaining label */
    public String getDaysLabel() {
        return daysLabel;
    }

    /** @param daysLabel days-remaining label */
    public void setDaysLabel(String daysLabel) {
        this.daysLabel = daysLabel;
    }

    /** @return CSS style class */
    public String getReminderClass() {
        return reminderClass;
    }

    /** @param reminderClass CSS style class */
    public void setReminderClass(String reminderClass) {
        this.reminderClass = reminderClass;
    }

    /** @return action link for navigation */
    public String getActionUrl() {
        return actionUrl;
    }

    /** @param actionUrl action link for navigation */
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
