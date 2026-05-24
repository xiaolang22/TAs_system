package com.group19.dto;

import com.group19.model.Job;
import java.util.ArrayList;
import java.util.List;

/**
 * Applicant review page data transfer object (DTO).
 * <p>
 * Encapsulates all data required when an MO (Module Organiser) views the applicant review page
 * for a specific job, including job information, the list of applicants (represented as
 * {@link ApplicantReviewRow} objects), and the current sort mode and sort label.
 * </p>
 *
 * @author Group 19
 */
public class ApplicantReviewPageData {

    /** The job currently under review */
    private Job job;

    /** List of applicant rows, each corresponding to one application record */
    private List<ApplicantReviewRow> applicants = new ArrayList<>();

    /** The current sort mode identifier (e.g., score, date) */
    private String sortMode;

    /** The display label for the current sort mode */
    private String sortLabel;

    /** @return the job information */
    public Job getJob() {
        return job;
    }

    /** @param job the job information */
    public void setJob(Job job) {
        this.job = job;
    }

    /** @return list of applicant rows */
    public List<ApplicantReviewRow> getApplicants() {
        return applicants;
    }

    /** @param applicants list of applicant rows */
    public void setApplicants(List<ApplicantReviewRow> applicants) {
        this.applicants = applicants == null ? new ArrayList<>() : applicants;
    }

    /** @return the sort mode identifier */
    public String getSortMode() {
        return sortMode;
    }

    /** @param sortMode the sort mode identifier */
    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
    }

    /** @return the sort mode display label */
    public String getSortLabel() {
        return sortLabel;
    }

    /** @param sortLabel the sort mode display label */
    public void setSortLabel(String sortLabel) {
        this.sortLabel = sortLabel;
    }
}
