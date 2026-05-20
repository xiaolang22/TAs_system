package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dao.TADao;
import com.group19.dto.TARecommendation;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.TA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RecommendationService {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "good",
            "in", "is", "of", "on", "or", "preferred", "previous", "the", "to", "with",
            "skill", "skills", "experience", "experiences", "proficiency", "ta",
            "teaching", "assistant"));

    private final TADao taDao;
    private final ApplicationDao applicationDao;

    public RecommendationService(TADao taDao, ApplicationDao applicationDao) {
        this.taDao = taDao;
        this.applicationDao = applicationDao;
    }

    public List<TARecommendation> recommendApplicants(Job job, List<Application> applications) {
        if (job == null || applications == null || applications.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> requiredSkills = tokenize(job.getRequirements());
        Map<String, TA> taByStudentId = loadTaProfiles();
        Map<String, Integer> acceptedWorkloadMap = buildAcceptedWorkloadMap();

        int minAcceptedCount = Integer.MAX_VALUE;
        int maxAcceptedCount = Integer.MIN_VALUE;
        for (Application application : applications) {
            int acceptedCount = acceptedWorkloadMap.getOrDefault(normalizeKey(application.getTaStudentId()), 0);
            minAcceptedCount = Math.min(minAcceptedCount, acceptedCount);
            maxAcceptedCount = Math.max(maxAcceptedCount, acceptedCount);
        }

        if (minAcceptedCount == Integer.MAX_VALUE) {
            minAcceptedCount = 0;
            maxAcceptedCount = 0;
        }

        List<TARecommendation> recommendations = new ArrayList<>();
        for (Application application : applications) {
            String studentIdKey = normalizeKey(application.getTaStudentId());
            TA taProfile = taByStudentId.get(studentIdKey);
            List<String> taSkills = taProfile == null ? Collections.emptyList() : tokenize(taProfile.getSkills());
            List<String> matchedSkills = findMatchedSkills(requiredSkills, taSkills);
            List<String> missingSkills = findMissingSkills(requiredSkills, taSkills);

            double skillMatchScore = requiredSkills.isEmpty()
                    ? 1.0
                    : (double) matchedSkills.size() / requiredSkills.size();

            int acceptedWorkloadCount = acceptedWorkloadMap.getOrDefault(studentIdKey, 0);
            double workloadScore = calculateWorkloadScore(
                    acceptedWorkloadCount,
                    minAcceptedCount,
                    maxAcceptedCount);
            double finalRecommendationScore = (0.7 * skillMatchScore) + (0.3 * workloadScore);

            String taName = application.getTaName() == null || application.getTaName().isBlank()
                    ? "Unknown TA"
                    : application.getTaName().trim();

            recommendations.add(new TARecommendation(
                    application.getApplicationId(),
                    application.getTaStudentId(),
                    taName,
                    skillMatchScore,
                    workloadScore,
                    finalRecommendationScore,
                    acceptedWorkloadCount,
                    matchedSkills,
                    missingSkills,
                    buildExplanation(matchedSkills, missingSkills, acceptedWorkloadCount, skillMatchScore, workloadScore)));
        }

        recommendations.sort(Comparator
                .comparingDouble(TARecommendation::getFinalRecommendationScore).reversed()
                .thenComparingDouble(TARecommendation::getSkillMatchScore).reversed()
                .thenComparingInt(TARecommendation::getAcceptedWorkloadCount)
                .thenComparing(TARecommendation::getTaName, String.CASE_INSENSITIVE_ORDER));
        return recommendations;
    }

    private Map<String, TA> loadTaProfiles() {
        Map<String, TA> result = new HashMap<>();
        try {
            for (TA ta : taDao.findAll()) {
                String studentIdKey = normalizeKey(ta.getStudentId());
                if (!studentIdKey.isEmpty()) {
                    result.put(studentIdKey, ta);
                }
            }
        } catch (IOException e) {
            return new HashMap<>();
        }
        return result;
    }

    private Map<String, Integer> buildAcceptedWorkloadMap() {
        Map<String, Integer> result = new HashMap<>();
        for (Application application : applicationDao.findAll()) {
            if (!"ACCEPTED".equalsIgnoreCase(application.getStatus())) {
                continue;
            }

            String studentIdKey = normalizeKey(application.getTaStudentId());
            if (studentIdKey.isEmpty()) {
                continue;
            }

            result.put(studentIdKey, result.getOrDefault(studentIdKey, 0) + 1);
        }
        return result;
    }

    private double calculateWorkloadScore(int acceptedWorkloadCount, int minAcceptedCount, int maxAcceptedCount) {
        if (maxAcceptedCount <= minAcceptedCount) {
            return 1.0;
        }
        return 1.0 - ((double) (acceptedWorkloadCount - minAcceptedCount) / (maxAcceptedCount - minAcceptedCount));
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        Set<String> tokens = new LinkedHashSet<>();
        for (String rawToken : text.toLowerCase(Locale.ENGLISH).split("[^a-z0-9]+")) {
            String token = rawToken.trim();
            if (token.isEmpty() || STOP_WORDS.contains(token) || isDigitsOnly(token)) {
                continue;
            }
            tokens.add(token);
        }
        return new ArrayList<>(tokens);
    }

    private List<String> findMatchedSkills(List<String> requiredSkills, List<String> taSkills) {
        Set<String> taSkillSet = new HashSet<>(taSkills);
        List<String> matched = new ArrayList<>();
        for (String requiredSkill : requiredSkills) {
            if (taSkillSet.contains(requiredSkill)) {
                matched.add(requiredSkill);
            }
        }
        return matched;
    }

    private List<String> findMissingSkills(List<String> requiredSkills, List<String> taSkills) {
        Set<String> taSkillSet = new HashSet<>(taSkills);
        List<String> missing = new ArrayList<>();
        for (String requiredSkill : requiredSkills) {
            if (!taSkillSet.contains(requiredSkill)) {
                missing.add(requiredSkill);
            }
        }
        return missing;
    }

    private String buildExplanation(
            List<String> matchedSkills,
            List<String> missingSkills,
            int acceptedWorkloadCount,
            double skillMatchScore,
            double workloadScore) {
        StringBuilder explanation = new StringBuilder();
        explanation.append("Matched skills: ").append(joinSkills(matchedSkills)).append(". ");
        explanation.append("Missing skills: ").append(joinSkills(missingSkills)).append(". ");
        explanation.append("Current workload: ").append(acceptedWorkloadCount)
                .append(acceptedWorkloadCount == 1 ? " accepted job. " : " accepted jobs. ");

        if (skillMatchScore >= 0.7 && workloadScore >= 0.7) {
            explanation.append("Recommended because this TA has a strong skill match and relatively low workload.");
        } else if (skillMatchScore >= 0.7) {
            explanation.append("Recommended mainly because the skill match is strong.");
        } else if (workloadScore >= 0.7) {
            explanation.append("Recommended mainly because the current workload is relatively low.");
        } else {
            explanation.append("Recommended as a balanced option among the current applicants.");
        }

        return explanation.toString();
    }

    private String joinSkills(List<String> skills) {
        return skills == null || skills.isEmpty() ? "none" : String.join(", ", skills);
    }

    private boolean isDigitsOnly(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }
}
