package com.group19.dto;

import com.group19.model.Job;
import java.util.ArrayList;
import java.util.List;

public class ApplicantReviewPageData {
    private Job job;
    private List<ApplicantReviewRow> applicants = new ArrayList<>();
    private String sortMode;
    private String sortLabel;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public List<ApplicantReviewRow> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantReviewRow> applicants) {
        this.applicants = applicants == null ? new ArrayList<>() : applicants;
    }

    public String getSortMode() {
        return sortMode;
    }

    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
    }

    public String getSortLabel() {
        return sortLabel;
    }

    public void setSortLabel(String sortLabel) {
        this.sortLabel = sortLabel;
    }
}
