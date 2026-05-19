package com.group19.dto;

public class AssignedPositionDto {
    private String applicationId;
    private String jobId;
    private String title;
    private String category;
    private String hoursText;
    private double parsedHours;
    private boolean hoursCounted;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHoursText() {
        return hoursText;
    }

    public void setHoursText(String hoursText) {
        this.hoursText = hoursText;
    }

    public double getParsedHours() {
        return parsedHours;
    }

    public void setParsedHours(double parsedHours) {
        this.parsedHours = parsedHours;
    }

    public boolean isHoursCounted() {
        return hoursCounted;
    }

    public void setHoursCounted(boolean hoursCounted) {
        this.hoursCounted = hoursCounted;
    }
}
