package com.group19.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TARecommendation {
    private final String applicationId;
    private final String taStudentId;
    private final String taName;
    private final double skillMatchScore;
    private final double workloadScore;
    private final double finalRecommendationScore;
    private final int acceptedWorkloadCount;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String explanation;

    public TARecommendation(
            String applicationId,
            String taStudentId,
            String taName,
            double skillMatchScore,
            double workloadScore,
            double finalRecommendationScore,
            int acceptedWorkloadCount,
            List<String> matchedSkills,
            List<String> missingSkills,
            String explanation) {
        this.applicationId = applicationId;
        this.taStudentId = taStudentId;
        this.taName = taName;
        this.skillMatchScore = skillMatchScore;
        this.workloadScore = workloadScore;
        this.finalRecommendationScore = finalRecommendationScore;
        this.acceptedWorkloadCount = acceptedWorkloadCount;
        this.matchedSkills = Collections.unmodifiableList(
                new ArrayList<>(matchedSkills == null ? Collections.emptyList() : matchedSkills));
        this.missingSkills = Collections.unmodifiableList(
                new ArrayList<>(missingSkills == null ? Collections.emptyList() : missingSkills));
        this.explanation = explanation;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getTaStudentId() {
        return taStudentId;
    }

    public String getTaName() {
        return taName;
    }

    public double getSkillMatchScore() {
        return skillMatchScore;
    }

    public double getWorkloadScore() {
        return workloadScore;
    }

    public double getFinalRecommendationScore() {
        return finalRecommendationScore;
    }

    public int getAcceptedWorkloadCount() {
        return acceptedWorkloadCount;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public String getExplanation() {
        return explanation;
    }
}
