package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dto.ApplicantReviewRow;
import com.group19.dto.TARecommendation;
import com.group19.model.Application;
import com.group19.model.Job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * TA recommendation service responsible for automatically generating ranking
 * recommendations based on position requirements and applicant qualifications.
 * Combines skill matching (weight 70%) and workload (weight 30%) to compute
 * a final score.
 *
 * @author Group19
 * @since 1.0
 */
public class RecommendationService {
    private static final double SKILL_MATCH_WEIGHT = 0.7;
    private static final double WORKLOAD_WEIGHT = 0.3;

    private final ApplicationDao applicationDao;

    public RecommendationService(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public List<TARecommendation> recommend(Job job, List<ApplicantReviewRow> applicants) {
        List<TARecommendation> recommendations = new ArrayList<>();
        if (job == null || applicants == null || applicants.isEmpty()) {
            return recommendations;
        }

        Map<String, Integer> acceptedWorkloadMap = buildAcceptedWorkloadMap();
        for (ApplicantReviewRow applicant : applicants) {
            recommendations.add(buildRecommendation(applicant,
                    acceptedWorkloadMap.getOrDefault(normalizeKey(applicant.getTaStudentId()), 0)));
        }

        recommendations.sort(Comparator
                .comparingDouble(TARecommendation::getFinalScore).reversed()
                .thenComparingInt(TARecommendation::getCurrentWorkload)
                .thenComparing(recommendation -> safe(recommendation.getTaName()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(recommendation -> safe(recommendation.getTaStudentId()), String.CASE_INSENSITIVE_ORDER));
        return recommendations;
    }

    private Map<String, Integer> buildAcceptedWorkloadMap() {
        Map<String, Integer> workloadMap = new HashMap<>();
        for (Application application : applicationDao.findAll()) {
            if (!isAccepted(application)) {
                continue;
            }

            String studentId = normalizeKey(application.getTaStudentId());
            if (studentId.isEmpty()) {
                continue;
            }
            workloadMap.put(studentId, workloadMap.getOrDefault(studentId, 0) + 1);
        }
        return workloadMap;
    }

    private TARecommendation buildRecommendation(ApplicantReviewRow applicant, int acceptedWorkload) {
        TARecommendation recommendation = new TARecommendation();
        recommendation.setTaStudentId(safe(applicant.getTaStudentId()));
        recommendation.setTaName(safe(applicant.getTaName()));
        recommendation.setMatchedSkillsText(safe(applicant.getMatchedSkillsText()));
        recommendation.setMissingSkillsText(safe(applicant.getMissingSkillsText()));
        recommendation.setCurrentWorkload(acceptedWorkload);
        recommendation.setCurrentWorkloadLabel(buildWorkloadLabel(acceptedWorkload));

        int matchedSkillCount = countSkills(applicant.getMatchedSkillsText());
        int missingSkillCount = countSkills(applicant.getMissingSkillsText());
        int totalRequiredSkillCount = matchedSkillCount + missingSkillCount;
        double skillMatchScore = totalRequiredSkillCount == 0
                ? 0.0
                : matchedSkillCount / (double) totalRequiredSkillCount;
        double workloadScore = 1.0 / (acceptedWorkload + 1.0);
        double finalScore = (SKILL_MATCH_WEIGHT * skillMatchScore) + (WORKLOAD_WEIGHT * workloadScore);

        recommendation.setMatchedRequiredSkillCount(matchedSkillCount);
        recommendation.setMissingRequiredSkillCount(missingSkillCount);
        recommendation.setTotalRequiredSkillCount(totalRequiredSkillCount);
        recommendation.setSkillMatchScore(skillMatchScore);
        recommendation.setWorkloadScore(workloadScore);
        recommendation.setFinalScore(finalScore);
        recommendation.setExplanation(buildExplanation(matchedSkillCount, totalRequiredSkillCount,
                recommendation.getCurrentWorkloadLabel()));
        return recommendation;
    }

    private static String buildExplanation(int matchedSkillCount, int totalRequiredSkillCount, String workloadLabel) {
        if (totalRequiredSkillCount <= 0) {
            return "No valid job requirements were extracted, so ranking is mainly based on accepted workload.";
        }
        if (matchedSkillCount == totalRequiredSkillCount) {
            return "All " + totalRequiredSkillCount
                    + " required skills are matched, with a current workload of " + workloadLabel + ".";
        }
        return "Matched " + matchedSkillCount + " of " + totalRequiredSkillCount
                + " required skills, with a current workload of " + workloadLabel + ".";
    }

    private static int countSkills(String skillsText) {
        if (skillsText == null || skillsText.isBlank()) {
            return 0;
        }

        int count = 0;
        for (String token : skillsText.split(",")) {
            if (!token.isBlank()) {
                count++;
            }
        }
        return count;
    }

    private static boolean isAccepted(Application application) {
        return application != null && "accepted".equals(normalizeKey(application.getStatus()));
    }

    private static String buildWorkloadLabel(int workloadCount) {
        return workloadCount + " accepted jobs";
    }

    private static String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
