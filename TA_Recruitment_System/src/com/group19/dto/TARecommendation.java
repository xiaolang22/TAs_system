package com.group19.dto;

public class TARecommendation {
    private String taStudentId;
    private String taName;
    private String matchedSkillsText;
    private String missingSkillsText;
    private int matchedRequiredSkillCount;
    private int missingRequiredSkillCount;
    private int totalRequiredSkillCount;
    private int currentWorkload;
    private String currentWorkloadLabel;
    private double skillMatchScore;
    private double workloadScore;
    private double finalScore;
    private String explanation;

    public String getTaStudentId() {
        return taStudentId;
    }

    public void setTaStudentId(String taStudentId) {
        this.taStudentId = taStudentId;
    }

    public String getTaName() {
        return taName;
    }

    public void setTaName(String taName) {
        this.taName = taName;
    }

    public String getMatchedSkillsText() {
        return matchedSkillsText;
    }

    public void setMatchedSkillsText(String matchedSkillsText) {
        this.matchedSkillsText = matchedSkillsText;
    }

    public String getMissingSkillsText() {
        return missingSkillsText;
    }

    public void setMissingSkillsText(String missingSkillsText) {
        this.missingSkillsText = missingSkillsText;
    }

    public int getMatchedRequiredSkillCount() {
        return matchedRequiredSkillCount;
    }

    public void setMatchedRequiredSkillCount(int matchedRequiredSkillCount) {
        this.matchedRequiredSkillCount = matchedRequiredSkillCount;
    }

    public int getMissingRequiredSkillCount() {
        return missingRequiredSkillCount;
    }

    public void setMissingRequiredSkillCount(int missingRequiredSkillCount) {
        this.missingRequiredSkillCount = missingRequiredSkillCount;
    }

    public int getTotalRequiredSkillCount() {
        return totalRequiredSkillCount;
    }

    public void setTotalRequiredSkillCount(int totalRequiredSkillCount) {
        this.totalRequiredSkillCount = totalRequiredSkillCount;
    }

    public int getCurrentWorkload() {
        return currentWorkload;
    }

    public void setCurrentWorkload(int currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    public String getCurrentWorkloadLabel() {
        return currentWorkloadLabel;
    }

    public void setCurrentWorkloadLabel(String currentWorkloadLabel) {
        this.currentWorkloadLabel = currentWorkloadLabel;
    }

    public double getSkillMatchScore() {
        return skillMatchScore;
    }

    public void setSkillMatchScore(double skillMatchScore) {
        this.skillMatchScore = skillMatchScore;
    }

    public double getWorkloadScore() {
        return workloadScore;
    }

    public void setWorkloadScore(double workloadScore) {
        this.workloadScore = workloadScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
