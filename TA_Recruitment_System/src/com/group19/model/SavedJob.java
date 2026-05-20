package com.group19.model;

public class SavedJob {
    private String userId;
    private String jobId;
    private String savedAt;

    public SavedJob() {
    }

    public SavedJob(String userId, String jobId, String savedAt) {
        this.userId = userId;
        this.jobId = jobId;
        this.savedAt = savedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt = savedAt;
    }
}
