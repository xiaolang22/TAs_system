package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.ApplicantReviewPageData;
import com.group19.dto.ApplicantReviewRow;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.TA;

import java.io.IOException;
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

public class ApplicantReviewService {
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "be", "by", "for", "from", "good", "help", "helping",
            "in", "is", "it", "knowledge", "of", "on", "or", "previous", "preferred", "proficiency",
            "skill", "skills", "strong", "support", "the", "to", "with", "able", "ability", "experience",
            "basic", "basics", "maintain", "maintaining", "reliable", "punctual");

    private final ApplicationDao applicationDao;
    private final TADao taDao;
    private final JobDao jobDao;

    public ApplicantReviewService(ApplicationDao applicationDao, TADao taDao, JobDao jobDao) {
        this.applicationDao = applicationDao;
        this.taDao = taDao;
        this.jobDao = jobDao;
    }

    public ServiceResult<ApplicantReviewPageData> loadApplicantsForJob(String jobId, String sortMode) {
        if (jobId == null || jobId.isBlank()) {
            return ServiceResult.failure("Job ID is required.");
        }

        Job job = jobDao.findById(jobId.trim());
        if (job == null) {
            return ServiceResult.failure("Job not found.");
        }

        String normalizedSortMode = normalizeSortMode(sortMode);
        Map<String, TA> taMap = loadTaMap();
        Map<String, Integer> workloadMap = buildWorkloadMap();
        List<String> requirementTokens = tokenize(job.getRequirements());

        List<ApplicantReviewRow> rows = new ArrayList<>();
        for (Application application : applicationDao.findByJobId(job.getJobId())) {
            rows.add(buildRow(application, taMap.get(normalizeKey(application.getTaStudentId())),
                    requirementTokens, workloadMap.getOrDefault(normalizeKey(application.getTaStudentId()), 0)));
        }

        sortRows(rows, normalizedSortMode);

        ApplicantReviewPageData pageData = new ApplicantReviewPageData();
        pageData.setJob(job);
        pageData.setApplicants(rows);
        pageData.setSortMode(normalizedSortMode);
        pageData.setSortLabel("status".equals(normalizedSortMode) ? "Status" : "Match degree");
        return ServiceResult.success(pageData, "Applicant review data loaded.");
    }

    private ApplicantReviewRow buildRow(
            Application application,
            TA profile,
            List<String> requirementTokens,
            int workloadCount) {
        ApplicantReviewRow row = new ApplicantReviewRow();
        row.setApplicationId(valueOrEmpty(application.getApplicationId()));
        row.setJobId(valueOrEmpty(application.getJobId()));
        row.setTaStudentId(valueOrEmpty(application.getTaStudentId()));
        row.setTaName(firstNonBlank(profile == null ? null : profile.getName(), application.getTaName()));
        row.setCvFilePath(firstNonBlank(profile == null ? null : profile.getCvFilePath(), application.getCvFilePath()));
        row.setStatus(valueOrEmpty(application.getStatus()));
        row.setSubmittedAt(valueOrEmpty(application.getSubmittedAt()));
        row.setUpdatedAt(valueOrEmpty(application.getUpdatedAt()));
        row.setDecisionNote(valueOrEmpty(application.getDecisionNote()));
        row.setProgramme(valueOrEmpty(profile == null ? null : profile.getProgramme()));
        row.setSkills(valueOrEmpty(profile == null ? null : profile.getSkills()));
        row.setExperience(valueOrEmpty(profile == null ? null : profile.getExperience()));
        row.setAvailability(valueOrEmpty(profile == null ? null : profile.getAvailability()));
        row.setCurrentWorkload(workloadCount);
        row.setCurrentWorkloadLabel(workloadCount + (workloadCount == 1 ? " active application" : " active applications"));

        List<String> skillTokens = tokenize(row.getSkills());
        int score = calculateMatchScore(requirementTokens, skillTokens);
        row.setMatchScore(score);
        row.setCoreSkillsHtml(buildCoreSkillsHtml(row.getSkills(), requirementTokens));
        row.setMatchedSkillsText(joinTokens(intersection(requirementTokens, skillTokens)));
        row.setMissingSkillsText(joinTokens(difference(requirementTokens, skillTokens)));
        applySkillStatus(row, profile, score);
        return row;
    }

    private Map<String, TA> loadTaMap() {
        Map<String, TA> taMap = new HashMap<>();
        try {
            for (TA ta : taDao.findAll()) {
                if (ta.getStudentId() != null && !ta.getStudentId().isBlank()) {
                    taMap.put(normalizeKey(ta.getStudentId()), ta);
                }
            }
        } catch (IOException ignored) {
            // Continue with application records when TA data cannot be loaded.
        }
        return taMap;
    }

    private Map<String, Integer> buildWorkloadMap() {
        Map<String, Integer> workloadMap = new HashMap<>();
        for (Application application : applicationDao.findAll()) {
            if (!isActiveStatus(application.getStatus())) {
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

    private void sortRows(List<ApplicantReviewRow> rows, String sortMode) {
        Comparator<ApplicantReviewRow> comparator;
        if ("status".equals(sortMode)) {
            comparator = Comparator
                    .comparingInt((ApplicantReviewRow row) -> statusRank(row.getStatus()))
                    .thenComparing(Comparator.comparingInt(ApplicantReviewRow::getMatchScore).reversed())
                    .thenComparing(row -> valueOrEmpty(row.getTaName()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(row -> valueOrEmpty(row.getTaStudentId()), String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = Comparator
                    .comparingInt(ApplicantReviewRow::getMatchScore).reversed()
                    .thenComparingInt(row -> statusRank(row.getStatus()))
                    .thenComparing(row -> valueOrEmpty(row.getTaName()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(row -> valueOrEmpty(row.getTaStudentId()), String.CASE_INSENSITIVE_ORDER);
        }
        rows.sort(comparator);
    }

    private static String normalizeSortMode(String sortMode) {
        if ("status".equalsIgnoreCase(sortMode)) {
            return "status";
        }
        return "match";
    }

    private static int statusRank(String status) {
        String normalized = normalizeKey(status);
        return switch (normalized) {
            case "submitted" -> 0;
            case "in_review" -> 1;
            case "shortlisted" -> 2;
            case "accepted" -> 3;
            case "rejected" -> 4;
            default -> 5;
        };
    }

    private static boolean isActiveStatus(String status) {
        String normalized = normalizeKey(status);
        return normalized.isEmpty() || !"rejected".equals(normalized);
    }

    private static void applySkillStatus(ApplicantReviewRow row, TA profile, int score) {
        if (profile == null || row.getSkills().isBlank()) {
            row.setCoreSkillStatusLabel("No core skills provided");
            row.setCoreSkillStatusClass("tag-neutral");
            return;
        }
        row.setCoreSkillStatusLabel("Core skills listed");
        row.setCoreSkillStatusClass("tag-neutral");
    }

    private static String buildCoreSkillsHtml(String rawSkills, List<String> requirementTokens) {
        List<String> skills = splitSkills(rawSkills);
        if (skills.isEmpty()) {
            return "<span class=\"muted\">Not provided</span>";
        }

        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        StringBuilder html = new StringBuilder();
        for (String skill : skills) {
            String normalizedSkill = normalizeKey(skill);
            boolean matched = matchesRequirement(normalizedSkill, requirementSet);
            html.append("<span class=\"skill-token");
            if (matched) {
                html.append(" skill-token-match");
            }
            html.append("\">")
                    .append(escapeHtml(skill))
                    .append("</span>");
        }
        return html.toString();
    }

    private static boolean matchesRequirement(String normalizedSkill, Set<String> requirementSet) {
        if (normalizedSkill.isEmpty() || requirementSet.isEmpty()) {
            return false;
        }
        if (requirementSet.contains(normalizedSkill)) {
            return true;
        }
        List<String> parts = tokenize(normalizedSkill);
        for (String part : parts) {
            if (requirementSet.contains(part)) {
                return true;
            }
        }
        return false;
    }

    private static int calculateMatchScore(List<String> requirementTokens, List<String> skillTokens) {
        if (requirementTokens.isEmpty() || skillTokens.isEmpty()) {
            return 0;
        }
        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        int matched = 0;
        for (String requirement : requirementSet) {
            if (skillSet.contains(requirement)) {
                matched++;
            }
        }
        return (int) Math.round((matched * 100.0) / requirementSet.size());
    }

    private static List<String> intersection(List<String> requirementTokens, List<String> skillTokens) {
        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        List<String> result = new ArrayList<>();
        for (String skill : skillSet) {
            if (requirementSet.contains(skill)) {
                result.add(skill);
            }
        }
        return result;
    }

    private static List<String> difference(List<String> requirementTokens, List<String> skillTokens) {
        Set<String> requirementSet = new LinkedHashSet<>(requirementTokens);
        Set<String> skillSet = new LinkedHashSet<>(skillTokens);
        List<String> result = new ArrayList<>();
        for (String requirement : requirementSet) {
            if (!skillSet.contains(requirement)) {
                result.add(requirement);
            }
        }
        return result;
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
                .collect(Collectors.toList());
    }

    private static List<String> splitSkills(String rawSkills) {
        if (rawSkills == null || rawSkills.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(rawSkills.split("[,;\\n]"))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
    }

    private static String joinTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "";
        }
        return String.join(", ", tokens);
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        return valueOrEmpty(second);
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        String result = value.replace("&", "&amp;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        return result.replace("'", "&#39;");
    }
}
