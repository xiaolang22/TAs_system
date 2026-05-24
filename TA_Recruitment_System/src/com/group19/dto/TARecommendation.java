package com.group19.dto;

/**
 * TA recommendation result data transfer object (DTO).
 * <p>
 * Encapsulates the system-generated TA job recommendation result, including the
 * recommended TA's basic information, skill match details (matched/missing skills
 * and counts), skill match score, workload score, composite final score, and a
 * recommendation explanation. Used as a reference when the admin assigns TAs.
 * </p>
 *
 * @author Group 19
 */
public class TARecommendation {

    /** Student ID of the recommended TA */
    private String taStudentId;

    /** Name of the recommended TA */
    private String taName;

    /** Text description of matched skills */
    private String matchedSkillsText;

    /** Text description of missing skills */
    private String missingSkillsText;

    /** Number of matched required skills */
    private int matchedRequiredSkillCount;

    /** Number of missing required skills */
    private int missingRequiredSkillCount;

    /** Total number of required skills for the job */
    private int totalRequiredSkillCount;

    /** Current workload (in hours) */
    private int currentWorkload;

    /** Display label for current workload */
    private String currentWorkloadLabel;

    /** Skill match score (0--100) */
    private double skillMatchScore;

    /** Workload score */
    private double workloadScore;

    /** Composite final score */
    private double finalScore;

    /** Recommendation explanation */
    private String explanation;

    /** @return TA student ID */
    public String getTaStudentId() {
        return taStudentId;
    }

    /** @param taStudentId TA student ID */
    public void setTaStudentId(String taStudentId) {
        this.taStudentId = taStudentId;
    }

    /** @return TA name */
    public String getTaName() {
        return taName;
    }

    /** @param taName TA name */
    public void setTaName(String taName) {
        this.taName = taName;
    }

    /** @return text description of matched skills */
    public String getMatchedSkillsText() {
        return matchedSkillsText;
    }

    /** @param matchedSkillsText text description of matched skills */
    public void setMatchedSkillsText(String matchedSkillsText) {
        this.matchedSkillsText = matchedSkillsText;
    }

    /** @return text description of missing skills */
    public String getMissingSkillsText() {
        return missingSkillsText;
    }

    /** @param missingSkillsText text description of missing skills */
    public void setMissingSkillsText(String missingSkillsText) {
        this.missingSkillsText = missingSkillsText;
    }

    /** @return number of matched required skills */
    public int getMatchedRequiredSkillCount() {
        return matchedRequiredSkillCount;
    }

    /** @param matchedRequiredSkillCount number of matched required skills */
    public void setMatchedRequiredSkillCount(int matchedRequiredSkillCount) {
        this.matchedRequiredSkillCount = matchedRequiredSkillCount;
    }

    /** @return number of missing required skills */
    public int getMissingRequiredSkillCount() {
        return missingRequiredSkillCount;
    }

    /** @param missingRequiredSkillCount number of missing required skills */
    public void setMissingRequiredSkillCount(int missingRequiredSkillCount) {
        this.missingRequiredSkillCount = missingRequiredSkillCount;
    }

    /** @return total number of required skills for the job */
    public int getTotalRequiredSkillCount() {
        return totalRequiredSkillCount;
    }

    /** @param totalRequiredSkillCount total number of required skills for the job */
    public void setTotalRequiredSkillCount(int totalRequiredSkillCount) {
        this.totalRequiredSkillCount = totalRequiredSkillCount;
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

    /** @return skill match score */
    public double getSkillMatchScore() {
        return skillMatchScore;
    }

    /** @param skillMatchScore skill match score */
    public void setSkillMatchScore(double skillMatchScore) {
        this.skillMatchScore = skillMatchScore;
    }

    /** @return workload score */
    public double getWorkloadScore() {
        return workloadScore;
    }

    /** @param workloadScore workload score */
    public void setWorkloadScore(double workloadScore) {
        this.workloadScore = workloadScore;
    }

    /** @return composite final score */
    public double getFinalScore() {
        return finalScore;
    }

    /** @param finalScore composite final score */
    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    /** @return recommendation explanation */
    public String getExplanation() {
        return explanation;
    }

    /** @param explanation recommendation explanation */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
