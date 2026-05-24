package com.group19.dto;

/**
 * Applicant review row data transfer object (DTO).
 * <p>
 * Used for each row in the applicant review page table. Encapsulates complete information
 * about an applicant, including personal profile, application status, CV file path,
 * skill match score, core skill status display, and current workload --
 * fields required for front-end display.
 * </p>
 *
 * @author Group 19
 */
public class ApplicantReviewRow {

    /** Unique identifier of the application record */
    private String applicationId;

    /** ID of the job applied for */
    private String jobId;

    /** Student ID of the applicant (TA) */
    private String taStudentId;

    /** Name of the applicant */
    private String taName;

    /** CV file path */
    private String cvFilePath;

    /** Application status (e.g., pending, accepted, rejected) */
    private String status;

    /** Application submission time (formatted string) */
    private String submittedAt;

    /** Application last updated time (formatted string) */
    private String updatedAt;

    /** Review decision note */
    private String decisionNote;

    /** Programme of the applicant */
    private String programme;

    /** Skill description of the applicant */
    private String skills;

    /** Experience description of the applicant */
    private String experience;

    /** Availability description of the applicant */
    private String availability;

    /** Skill match score */
    private int matchScore;

    /** Label text for core skill status (e.g., "Full match", "Partial match") */
    private String coreSkillStatusLabel;

    /** CSS style class for core skill status */
    private String coreSkillStatusClass;

    /** HTML fragment for core skill display */
    private String coreSkillsHtml;

    /** Text description of matched skills */
    private String matchedSkillsText;

    /** Text description of missing skills */
    private String missingSkillsText;

    /** Current assigned workload of the applicant (in hours) */
    private int currentWorkload;

    /** Display label for current workload */
    private String currentWorkloadLabel;

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

    /** @return TA student ID */
    public String getTaStudentId() {
        return taStudentId;
    }

    /** @param taStudentId TA student ID */
    public void setTaStudentId(String taStudentId) {
        this.taStudentId = taStudentId;
    }

    /** @return applicant name */
    public String getTaName() {
        return taName;
    }

    /** @param taName applicant name */
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

    /** @return application submission time */
    public String getSubmittedAt() {
        return submittedAt;
    }

    /** @param submittedAt application submission time */
    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    /** @return application last updated time */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /** @param updatedAt application last updated time */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** @return review decision note */
    public String getDecisionNote() {
        return decisionNote;
    }

    /** @param decisionNote review decision note */
    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    /** @return programme */
    public String getProgramme() {
        return programme;
    }

    /** @param programme programme */
    public void setProgramme(String programme) {
        this.programme = programme;
    }

    /** @return skill description */
    public String getSkills() {
        return skills;
    }

    /** @param skills skill description */
    public void setSkills(String skills) {
        this.skills = skills;
    }

    /** @return experience description */
    public String getExperience() {
        return experience;
    }

    /** @param experience experience description */
    public void setExperience(String experience) {
        this.experience = experience;
    }

    /** @return availability description */
    public String getAvailability() {
        return availability;
    }

    /** @param availability availability description */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /** @return skill match score */
    public int getMatchScore() {
        return matchScore;
    }

    /** @param matchScore skill match score */
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    /** @return core skill status label text */
    public String getCoreSkillStatusLabel() {
        return coreSkillStatusLabel;
    }

    /** @param coreSkillStatusLabel core skill status label text */
    public void setCoreSkillStatusLabel(String coreSkillStatusLabel) {
        this.coreSkillStatusLabel = coreSkillStatusLabel;
    }

    /** @return CSS style class */
    public String getCoreSkillStatusClass() {
        return coreSkillStatusClass;
    }

    /** @param coreSkillStatusClass CSS style class */
    public void setCoreSkillStatusClass(String coreSkillStatusClass) {
        this.coreSkillStatusClass = coreSkillStatusClass;
    }

    /** @return core skills HTML fragment */
    public String getCoreSkillsHtml() {
        return coreSkillsHtml;
    }

    /** @param coreSkillsHtml core skills HTML fragment */
    public void setCoreSkillsHtml(String coreSkillsHtml) {
        this.coreSkillsHtml = coreSkillsHtml;
    }

    /** @return matched skills text description */
    public String getMatchedSkillsText() {
        return matchedSkillsText;
    }

    /** @param matchedSkillsText matched skills text description */
    public void setMatchedSkillsText(String matchedSkillsText) {
        this.matchedSkillsText = matchedSkillsText;
    }

    /** @return missing skills text description */
    public String getMissingSkillsText() {
        return missingSkillsText;
    }

    /** @param missingSkillsText missing skills text description */
    public void setMissingSkillsText(String missingSkillsText) {
        this.missingSkillsText = missingSkillsText;
    }

    /** @return current workload */
    public int getCurrentWorkload() {
        return currentWorkload;
    }

    /** @param currentWorkload current workload */
    public void setCurrentWorkload(int currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    /** @return workload display label */
    public String getCurrentWorkloadLabel() {
        return currentWorkloadLabel;
    }

    /** @param currentWorkloadLabel workload display label */
    public void setCurrentWorkloadLabel(String currentWorkloadLabel) {
        this.currentWorkloadLabel = currentWorkloadLabel;
    }
}
