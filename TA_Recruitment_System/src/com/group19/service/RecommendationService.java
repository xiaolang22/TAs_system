package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dto.TARecommendation;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.TA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RecommendationService {
    private static final double SKILL_MATCH_WEIGHT = 0.7;
    private static final double WORKLOAD_WEIGHT = 0.3;
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "be", "by", "for", "from", "good", "help", "helping",
            "in", "is", "it", "knowledge", "of", "on", "or", "previous", "preferred", "proficiency",
            "skill", "skills", "strong", "support", "the", "to", "with", "able", "ability", "experience",
            "basic", "basics", "maintain", "maintaining", "reliable", "punctual");

    private final ApplicationDao applicationDao;

    public RecommendationService(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public List<TARecommendation> recommend(Job job, List<TA> tas) {
        List<TARecommendation> recommendations = new ArrayList<>();
        if (job == null || tas == null || tas.isEmpty()) {
            return recommendations;
        }

        List<String> requirementTokens = tokenize(job.getRequirements());
        Map<String, Integer> acceptedWorkloadMap = buildAcceptedWorkloadMap();
        for (TA ta : tas) {
            if (ta == null) {
                continue;
            }
            recommendations.add(buildRecommendation(ta, requirementTokens,
                    acceptedWorkloadMap.getOrDefault(normalizeKey(ta.getStudentId()), 0)));
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

    private TARecommendation buildRecommendation(TA ta, List<String> requirementTokens, int acceptedWorkload) {
        TARecommendation recommendation = new TARecommendation();
        recommendation.setTaStudentId(safe(ta.getStudentId()));
        recommendation.setTaName(safe(ta.getName()));
        List<String> skillTokens = tokenize(ta.getSkills());
        List<String> matchedSkills = intersection(requirementTokens, skillTokens);
        List<String> missingSkills = difference(requirementTokens, skillTokens);
        recommendation.setMatchedSkillsText(joinTokens(matchedSkills));
        recommendation.setMissingSkillsText(joinTokens(missingSkills));
        recommendation.setCurrentWorkload(acceptedWorkload);
        recommendation.setCurrentWorkloadLabel(buildWorkloadLabel(acceptedWorkload));

        int matchedSkillCount = matchedSkills.size();
        int missingSkillCount = missingSkills.size();
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

    private static boolean isAccepted(Application application) {
        return application != null && "accepted".equals(normalizeKey(application.getStatus()));
    }

    private static String buildWorkloadLabel(int workloadCount) {
        return workloadCount + " accepted jobs";
    }

    private static String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> tokenize(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(raw.toLowerCase(Locale.ROOT).split("[^a-z0-9+]+"))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(token -> "c++".equals(token) ? "c" : token)
                .filter(token -> token.length() > 1 || "c".equals(token))
                .filter(token -> !STOP_WORDS.contains(token))
                .distinct()
                .collect(Collectors.toList());
    }

    private static List<String> intersection(List<String> requirementTokens, List<String> skillTokens) {
        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        List<String> result = new ArrayList<>();
        for (String requirement : requirementSet) {
            if (skillSet.contains(requirement)) {
                result.add(requirement);
            }
        }
        return result;
    }

    private static List<String> difference(List<String> requirementTokens, List<String> skillTokens) {
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        List<String> result = new ArrayList<>();
        for (String requirement : new LinkedHashSet<>(requirementTokens)) {
            if (!skillSet.contains(requirement)) {
                result.add(requirement);
            }
        }
        return result;
    }

    private static String joinTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "";
        }
        return String.join(", ", tokens);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
