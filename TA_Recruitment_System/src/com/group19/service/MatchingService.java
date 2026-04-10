package com.group19.service;

import com.group19.dao.TADao;
import com.group19.dto.CandidateMatchResult;
import com.group19.dto.ServiceResult;
import com.group19.model.TA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MatchingService {
    private final TADao taDao;

    public MatchingService(TADao taDao) {
        this.taDao = taDao;
    }

    public ServiceResult<List<CandidateMatchResult>> evaluateCandidates(String requiredSkillsText) {
        LinkedHashMap<String, String> requiredSkillsMap = parseSkillMap(requiredSkillsText);
        if (requiredSkillsMap.isEmpty()) {
            return ServiceResult.failure(
                    "Please enter at least one required skill (split by comma, semicolon, slash, or new line).");
        }

        try {
            List<TA> allCandidates = taDao.findAll();
            List<CandidateMatchResult> reviewList = new ArrayList<>();

            for (TA candidate : allCandidates) {
                reviewList.add(calculateMatch(candidate, requiredSkillsMap));
            }

            if (reviewList.isEmpty()) {
                return ServiceResult.failure(
                        "No TA profiles found. Ask TA users to fill profile skills first.");
            }

            reviewList.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));
            return ServiceResult.success(reviewList, "Match score calculated.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to load TA profile data.");
        }
    }

    private CandidateMatchResult calculateMatch(TA candidate, LinkedHashMap<String, String> requiredSkillsMap) {
        String candidateName = normalizeDisplay(candidate.getName(), "Unknown");
        String studentId = normalizeDisplay(candidate.getStudentId(), "-");
        String candidateSkillsText = normalizeDisplay(candidate.getSkills(), "-");

        Set<String> candidateSkillSet = parseSkillSet(candidate.getSkills());
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (Map.Entry<String, String> required : requiredSkillsMap.entrySet()) {
            if (candidateSkillSet.contains(required.getKey())) {
                matched.add(required.getValue());
            } else {
                missing.add(required.getValue());
            }
        }

        int totalRequired = requiredSkillsMap.size();
        int score = (int) Math.round((matched.size() * 100.0) / totalRequired);

        String note = missing.isEmpty()
                ? "Fully matched all required skills."
                : "Missing skills: " + String.join(", ", missing) + ".";

        return new CandidateMatchResult(
                candidateName,
                studentId,
                candidateSkillsText,
                score,
                matched,
                missing,
                note);
    }

    private static LinkedHashMap<String, String> parseSkillMap(String rawSkills) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        if (rawSkills == null || rawSkills.isBlank()) {
            return result;
        }

        for (String token : splitSkills(rawSkills)) {
            String cleaned = cleanSkillToken(token);
            if (cleaned.isEmpty()) {
                continue;
            }

            String normalized = normalizedSkill(cleaned);
            if (!result.containsKey(normalized)) {
                result.put(normalized, cleaned);
            }
        }
        return result;
    }

    private static Set<String> parseSkillSet(String rawSkills) {
        Set<String> result = new LinkedHashSet<>();
        if (rawSkills == null || rawSkills.isBlank()) {
            return result;
        }

        for (String token : splitSkills(rawSkills)) {
            String cleaned = cleanSkillToken(token);
            if (!cleaned.isEmpty()) {
                result.add(normalizedSkill(cleaned));
            }
        }
        return result;
    }

    private static String[] splitSkills(String rawSkills) {
        return rawSkills.split("[,，;；/\\\\\\n\\r]+");
    }

    private static String cleanSkillToken(String token) {
        if (token == null) {
            return "";
        }
        return token.trim().replaceAll("\\s+", " ");
    }

    private static String normalizedSkill(String token) {
        return token.toLowerCase(Locale.ROOT);
    }

    private static String normalizeDisplay(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}
