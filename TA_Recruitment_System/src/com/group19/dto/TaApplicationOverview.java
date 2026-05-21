package com.group19.dto;

import java.util.ArrayList;
import java.util.List;

public class TaApplicationOverview {
    private String applicationId;
    private String jobId;
    private String jobTitle;
    private String statusLabel;
    private String statusPillClass;
    private String lastUpdatedDisplay;
    private List<TaTimelineStep> timelineSteps = new ArrayList<>();

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getStatusPillClass() {
        return statusPillClass;
    }

    public void setStatusPillClass(String statusPillClass) {
        this.statusPillClass = statusPillClass;
    }

    public String getLastUpdatedDisplay() {
        return lastUpdatedDisplay;
    }

    public void setLastUpdatedDisplay(String lastUpdatedDisplay) {
        this.lastUpdatedDisplay = lastUpdatedDisplay;
    }

    public List<TaTimelineStep> getTimelineSteps() {
        return timelineSteps;
    }

    public void setTimelineSteps(List<TaTimelineStep> timelineSteps) {
        this.timelineSteps = timelineSteps == null ? new ArrayList<>() : timelineSteps;
    }
}
