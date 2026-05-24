package com.group19.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * TA application overview data transfer object (DTO).
 * <p>
 * Used on the TA-facing "My Applications" page. Displays an overview of each
 * application record, including job information, application status (with
 * front-end display style classes), last updated time, and the application's
 * timeline step list.
 * </p>
 *
 * @author Group 19
 */
public class TaApplicationOverview {

    /** Application record ID */
    private String applicationId;

    /** ID of the job applied for */
    private String jobId;

    /** Job title */
    private String jobTitle;

    /** Display label for the application status (e.g., "Pending", "Accepted") */
    private String statusLabel;

    /** CSS style class for the application status (e.g., "badge-warning", "badge-success") */
    private String statusPillClass;

    /** Display text for the last updated time */
    private String lastUpdatedDisplay;

    /** Timeline step list for the application, ordered by time */
    private List<TaTimelineStep> timelineSteps = new ArrayList<>();

    /** @return application record ID */
    public String getApplicationId() {
        return applicationId;
    }

    /** @param applicationId application record ID */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /** @return job ID */
    public String getJobId() {
        return jobId;
    }

    /** @param jobId job ID */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /** @return job title */
    public String getJobTitle() {
        return jobTitle;
    }

    /** @param jobTitle job title */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /** @return display label for the application status */
    public String getStatusLabel() {
        return statusLabel;
    }

    /** @param statusLabel display label for the application status */
    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    /** @return CSS style class */
    public String getStatusPillClass() {
        return statusPillClass;
    }

    /** @param statusPillClass CSS style class */
    public void setStatusPillClass(String statusPillClass) {
        this.statusPillClass = statusPillClass;
    }

    /** @return display text for the last updated time */
    public String getLastUpdatedDisplay() {
        return lastUpdatedDisplay;
    }

    /** @param lastUpdatedDisplay display text for the last updated time */
    public void setLastUpdatedDisplay(String lastUpdatedDisplay) {
        this.lastUpdatedDisplay = lastUpdatedDisplay;
    }

    /** @return timeline step list */
    public List<TaTimelineStep> getTimelineSteps() {
        return timelineSteps;
    }

    /** @param timelineSteps timeline step list */
    public void setTimelineSteps(List<TaTimelineStep> timelineSteps) {
        this.timelineSteps = timelineSteps == null ? new ArrayList<>() : timelineSteps;
    }
}
