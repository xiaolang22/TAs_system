package com.group19.model;

/**
 * TA application entity representing a student's application for a teaching assistant position.
 * Contains application status, CV file path, submission timestamp, and decision notes.
 *
 * @author Group19
 * @since 1.0
 */
public class Application {

    /** Unique identifier for the application */
    private String applicationId;

    /** Identifier of the associated job position */
    private String jobId;

    /** Student ID of the applicant (TA student) */
    private String taStudentId;

    /** Full name of the applicant */
    private String taName;

    /** File path to the uploaded CV */
    private String cvFilePath;

    /** Application status (e.g. "submitted" / "under_review" / "approved" / "rejected") */
    private String status;

    /** Timestamp when the application was submitted */
    private String submittedAt;

    /** Timestamp when the application was last updated */
    private String updatedAt;

    /** Decision note (reason for approval or rejection) */
    private String decisionNote;

    /**
     * Default no-argument constructor.
     */
    public Application() {
    }

    /** @return unique application identifier */
    public String getApplicationId() {
        return applicationId;
    }

    /** @param applicationId unique application identifier */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /** @return associated job position ID */
    public String getJobId() {
        return jobId;
    }

    /** @param jobId associated job position ID */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /** @return applicant's student ID */
    public String getTaStudentId() {
        return taStudentId;
    }

    /** @param taStudentId applicant's student ID */
    public void setTaStudentId(String taStudentId) {
        this.taStudentId = taStudentId;
    }

    /** @return applicant's full name */
    public String getTaName() {
        return taName;
    }

    /** @param taName applicant's full name */
    public void setTaName(String taName) {
        this.taName = taName;
    }

    /** @return CV file path */
    public String getCvFilePath() {
        return cvFilePath;
    }

    /** @param cvFilePath CV file path */
    public void setCvFilePath(String cvFilePath) {
        this.cvFilePath = cvFilePath;
    }

    /** @return application status */
    public String getStatus() {
        return status;
    }

    /** @param status application status */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return submission timestamp */
    public String getSubmittedAt() {
        return submittedAt;
    }

    /** @param submittedAt submission timestamp */
    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    /** @return last updated timestamp */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /** @param updatedAt last updated timestamp */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** @return decision note */
    public String getDecisionNote() {
        return decisionNote;
    }

    /** @param decisionNote decision note */
    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }
}