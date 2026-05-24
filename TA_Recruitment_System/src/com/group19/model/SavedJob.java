package com.group19.model;

/**
 * Saved job entity class, recording information about jobs saved by users.
 * Used to implement the job saving functionality for TA users.
 *
 * @author Group19
 * @since 1.0
 */
public class SavedJob {

    /** User ID */
    private String userId;

    /** ID of the saved job */
    private String jobId;

    /** Time when the job was saved */
    private String savedAt;

    /**
     * Default no-argument constructor.
     */
    public SavedJob() {
    }

    /**
     * Creates a saved job record.
     *
     * @param userId  User ID
     * @param jobId   Job ID
     * @param savedAt Time when saved
     */
    public SavedJob(String userId, String jobId, String savedAt) {
        this.userId = userId;
        this.jobId = jobId;
        this.savedAt = savedAt;
    }

    /** @return User ID */
    public String getUserId() { return userId; }
    /** @param userId User ID */
    public void setUserId(String userId) { this.userId = userId; }

    /** @return Job ID */
    public String getJobId() { return jobId; }
    /** @param jobId Job ID */
    public void setJobId(String jobId) { this.jobId = jobId; }

    /** @return Time when saved */
    public String getSavedAt() { return savedAt; }
    /** @param savedAt Time when saved */
    public void setSavedAt(String savedAt) { this.savedAt = savedAt; }
}
