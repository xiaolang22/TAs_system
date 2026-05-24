package com.group19.model;

/**
 * Job position entity class, recording TA position information published by the MO.
 * Contains fields such as job title, category, description, hours requirement,
 * deadline, and publication status.
 *
 * @author Group19
 * @since 1.0
 */
public class Job {

    /** Unique identifier for the job position */
    private String jobId;

    /** Job title */
    private String title;

    /** Job category (e.g. "Grad" / "PhD" / "Staff") */
    private String category;

    /** Detailed description of the job */
    private String description;

    /** Job requirements (skills, experience, etc.) */
    private String requirements;

    /** Weekly working hours */
    private String hours;

    /** Work schedule (e.g. specific shift times) */
    private String schedule;

    /** Application deadline */
    private String deadline;

    /** Job status (e.g. "open" / "closed") */
    private String status;

    /** Job creation time */
    private String createdAt;

    /** User ID of the MO who published this job */
    private String ownerMoUserId;

    /**
     * Default no-argument constructor.
     */
    public Job() {}

    /** @return Unique identifier for the job position */
    public String getJobId() { return jobId; }
    /** @param jobId Unique identifier for the job position */
    public void setJobId(String jobId) { this.jobId = jobId; }

    /** @return Job title */
    public String getTitle() { return title; }
    /** @param title Job title */
    public void setTitle(String title) { this.title = title; }

    /** @return Job category */
    public String getCategory() { return category; }
    /** @param category Job category */
    public void setCategory(String category) { this.category = category; }

    /** @return Detailed description of the job */
    public String getDescription() { return description; }
    /** @param description Detailed description of the job */
    public void setDescription(String description) { this.description = description; }

    /** @return Job requirements */
    public String getRequirements() { return requirements; }
    /** @param requirements Job requirements */
    public void setRequirements(String requirements) { this.requirements = requirements; }

    /** @return Weekly working hours */
    public String getHours() { return hours; }
    /** @param hours Weekly working hours */
    public void setHours(String hours) { this.hours = hours; }

    /** @return Work schedule */
    public String getSchedule() { return schedule; }
    /** @param schedule Work schedule */
    public void setSchedule(String schedule) { this.schedule = schedule; }

    /** @return Application deadline */
    public String getDeadline() { return deadline; }
    /** @param deadline Application deadline */
    public void setDeadline(String deadline) { this.deadline = deadline; }

    /** @return Job status */
    public String getStatus() { return status; }
    /** @param status Job status */
    public void setStatus(String status) { this.status = status; }

    /** @return Creation time */
    public String getCreatedAt() { return createdAt; }
    /** @param createdAt Creation time */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** @return User ID of the publisher (MO) */
    public String getOwnerMoUserId() { return ownerMoUserId; }
    /** @param ownerMoUserId User ID of the publisher (MO) */
    public void setOwnerMoUserId(String ownerMoUserId) { this.ownerMoUserId = ownerMoUserId; }
}
