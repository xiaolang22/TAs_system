package com.group19.dto;

/**
 * Assigned position data transfer object (DTO).
 * <p>
 * Describes a specific job position that has been assigned to a TA. Contains basic
 * job attributes (title, category, hours, schedule), parsed hours value, an hours-counted
 * flag, and a time conflict flag. Used on the workload monitoring page.
 * </p>
 *
 * @author Group 19
 */
public class AssignedPositionDto {

    /** Application record ID */
    private String applicationId;

    /** Job ID */
    private String jobId;

    /** Job title */
    private String title;

    /** Job category (e.g., Lab, Tutorial, Marking) */
    private String category;

    /** Hours text description (e.g., "10 hours/week") */
    private String hoursText;

    /** Schedule text description */
    private String scheduleText;

    /** Parsed hours value (in hours) */
    private double parsedHours;

    /** Whether the hours for this position are counted in the workload total */
    private boolean hoursCounted;

    /** Whether a time conflict exists */
    private boolean timeConflict;

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
    public String getTitle() {
        return title;
    }

    /** @param title job title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return job category */
    public String getCategory() {
        return category;
    }

    /** @param category job category */
    public void setCategory(String category) {
        this.category = category;
    }

    /** @return hours text description */
    public String getHoursText() {
        return hoursText;
    }

    /** @param hoursText hours text description */
    public void setHoursText(String hoursText) {
        this.hoursText = hoursText;
    }

    /** @return schedule text description */
    public String getScheduleText() {
        return scheduleText;
    }

    /** @param scheduleText schedule text description */
    public void setScheduleText(String scheduleText) {
        this.scheduleText = scheduleText;
    }

    /** @return parsed hours value */
    public double getParsedHours() {
        return parsedHours;
    }

    /** @param parsedHours parsed hours value */
    public void setParsedHours(double parsedHours) {
        this.parsedHours = parsedHours;
    }

    /** @return whether hours are counted in workload */
    public boolean isHoursCounted() {
        return hoursCounted;
    }

    /** @param hoursCounted whether hours are counted in workload */
    public void setHoursCounted(boolean hoursCounted) {
        this.hoursCounted = hoursCounted;
    }

    /** @return whether a time conflict exists */
    public boolean isTimeConflict() {
        return timeConflict;
    }

    /** @param timeConflict whether a time conflict exists */
    public void setTimeConflict(boolean timeConflict) {
        this.timeConflict = timeConflict;
    }
}
