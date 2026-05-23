package com.group19.service;

import com.group19.dao.TADao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.MoTaCandidateCard;
import com.group19.dto.ServiceResult;
import com.group19.model.TA;
import com.group19.model.UserAccount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MoTaDirectoryService {
    private final TADao taDao;
    private final UserAccountDao userAccountDao;

    public MoTaDirectoryService(TADao taDao, UserAccountDao userAccountDao) {
        this.taDao = taDao;
        this.userAccountDao = userAccountDao;
    }

    public ServiceResult<List<MoTaCandidateCard>> loadAllCandidates() {
        try {
            List<UserAccount> allAccounts = userAccountDao.findAll();
            List<TA> profiles = taDao.findAll();
            Map<String, TA> profileByStudentId = new LinkedHashMap<>();

            for (TA profile : profiles) {
                if (profile != null && hasText(profile.getStudentId())) {
                    profileByStudentId.put(profile.getStudentId().trim().toUpperCase(Locale.ROOT), profile);
                }
            }

            List<MoTaCandidateCard> cards = new ArrayList<>();
            for (UserAccount account : allAccounts) {
                if (account == null || !isTaRole(account.getRole())) {
                    continue;
                }
                TA profile = profileByStudentId.get(normalizeKey(account.getUserId()));
                cards.add(buildCard(account, profile));
            }

            cards.sort(Comparator
                    .comparing(MoTaCandidateCard::isProfileCompleted).reversed()
                    .thenComparing(card -> normalizeText(card.getDisplayName()))
                    .thenComparing(card -> normalizeText(card.getStudentId())));
            return ServiceResult.success(cards, "TA 列表加载成功。");
        } catch (IOException e) {
            return ServiceResult.failure("读取 TA 列表失败。");
        }
    }

    public ServiceResult<MoTaCandidateCard> loadCandidateByStudentId(String studentId) {
        if (!hasText(studentId)) {
            return ServiceResult.failure("缺少 TA 学号。");
        }
        ServiceResult<List<MoTaCandidateCard>> listResult = loadAllCandidates();
        if (!listResult.isSuccess()) {
            return ServiceResult.failure(listResult.getMessage());
        }
        String normalizedStudentId = studentId.trim();
        for (MoTaCandidateCard candidate : listResult.getData()) {
            if (candidate != null && normalizedStudentId.equalsIgnoreCase(candidate.getStudentId())) {
                return ServiceResult.success(candidate, "TA 档案加载成功。");
            }
        }
        return ServiceResult.failure("未找到对应 TA。");
    }

    public List<MoTaCandidateCard> filterCandidates(
            List<MoTaCandidateCard> source,
            String keyword,
            String programme,
            String availability) {
        List<MoTaCandidateCard> candidates = new ArrayList<>();
        if (source == null) {
            return candidates;
        }

        for (MoTaCandidateCard card : source) {
            if (card == null) {
                continue;
            }
            if (!matchesKeyword(card, keyword)) {
                continue;
            }
            if (!matchesText(card.getProgramme(), programme)) {
                continue;
            }
            if (!matchesText(card.getAvailability(), availability)) {
                continue;
            }
            candidates.add(card);
        }
        return candidates;
    }

    public ServiceResult<List<MoTaCandidateCard>> matchCandidates(
            List<MoTaCandidateCard> source,
            String requiredSkillsText) {
        LinkedHashMap<String, String> requiredSkillMap = parseSkillMap(requiredSkillsText);
        if (requiredSkillMap.isEmpty()) {
            return ServiceResult.failure("请至少输入一个候选人技能匹配关键词。");
        }

        List<MoTaCandidateCard> matchedCards = new ArrayList<>();
        if (source != null) {
            for (MoTaCandidateCard sourceCard : source) {
                if (sourceCard == null) {
                    continue;
                }
                matchedCards.add(copyWithMatchResult(sourceCard, requiredSkillMap));
            }
        }

        matchedCards.sort(Comparator
                .comparingInt(MoTaCandidateCard::getMatchScore).reversed()
                .thenComparing(Comparator.comparing(MoTaCandidateCard::isProfileCompleted).reversed())
                .thenComparing(card -> normalizeText(card.getDisplayName())));
        return ServiceResult.success(matchedCards, "匹配结果已更新。");
    }

    private static MoTaCandidateCard buildCard(UserAccount account, TA profile) {
        MoTaCandidateCard card = new MoTaCandidateCard();
        card.setDisplayName(firstNonBlank(
                profile == null ? null : profile.getName(),
                account.getDisplayName(),
                account.getUsername(),
                "TA"));
        card.setUsername(trimToEmpty(account.getUsername()));
        card.setStudentId(trimToEmpty(account.getUserId()));
        card.setEmail(profile == null ? "" : trimToEmpty(profile.getEmail()));
        card.setProgramme(profile == null ? "" : trimToEmpty(profile.getProgramme()));
        card.setSkills(profile == null ? "" : trimToEmpty(profile.getSkills()));
        card.setExperience(profile == null ? "" : trimToEmpty(profile.getExperience()));
        card.setAvailability(profile == null ? "" : trimToEmpty(profile.getAvailability()));
        card.setAvatarPath(trimToEmpty(account.getAvatarPath()));
        card.setCvFilePath(profile == null ? "" : trimToEmpty(profile.getCvFilePath()));
        card.setUpdatedAt(profile == null ? "" : trimToEmpty(profile.getUpdatedAt()));
        card.setProfileCompleted(profile != null);
        card.setResumeAvailable(false);
        card.setMatchScore(0);
        card.setMatchedSkillsText("");
        card.setMissingSkillsText("");
        card.setMatchNote(profile == null ? "该 TA 尚未完善个人档案。" : "可切换到技能匹配模式进行推荐。");
        return card;
    }

    private static MoTaCandidateCard copyWithMatchResult(
            MoTaCandidateCard source,
            LinkedHashMap<String, String> requiredSkillMap) {
        MoTaCandidateCard copy = new MoTaCandidateCard();
        copy.setDisplayName(source.getDisplayName());
        copy.setUsername(source.getUsername());
        copy.setStudentId(source.getStudentId());
        copy.setEmail(source.getEmail());
        copy.setProgramme(source.getProgramme());
        copy.setSkills(source.getSkills());
        copy.setExperience(source.getExperience());
        copy.setAvailability(source.getAvailability());
        copy.setAvatarPath(source.getAvatarPath());
        copy.setAvatarUrl(source.getAvatarUrl());
        copy.setCvFilePath(source.getCvFilePath());
        copy.setCvUrl(source.getCvUrl());
        copy.setUpdatedAt(source.getUpdatedAt());
        copy.setProfileCompleted(source.isProfileCompleted());
        copy.setResumeAvailable(source.isResumeAvailable());

        Set<String> candidateSkillSet = parseSkillSet(source.getSkills());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        for (Map.Entry<String, String> requiredSkill : requiredSkillMap.entrySet()) {
            if (candidateSkillSet.contains(requiredSkill.getKey())) {
                matchedSkills.add(requiredSkill.getValue());
            } else {
                missingSkills.add(requiredSkill.getValue());
            }
        }

        int score = requiredSkillMap.isEmpty()
                ? 0
                : (int) Math.round((matchedSkills.size() * 100.0) / requiredSkillMap.size());
        copy.setMatchScore(score);
        copy.setMatchedSkillsText(matchedSkills.isEmpty() ? "" : String.join("、", matchedSkills));
        copy.setMissingSkillsText(missingSkills.isEmpty() ? "" : String.join("、", missingSkills));
        if (requiredSkillMap.isEmpty()) {
            copy.setMatchNote("当前没有有效的匹配关键词。");
        } else if (missingSkills.isEmpty()) {
            copy.setMatchNote("已满足全部匹配技能要求。");
        } else {
            copy.setMatchNote("缺少技能：" + String.join("、", missingSkills) + "。");
        }
        return copy;
    }

    private static boolean matchesKeyword(MoTaCandidateCard card, String keyword) {
        if (!hasText(keyword)) {
            return true;
        }
        String expected = keyword.trim().toLowerCase(Locale.ROOT);
        return contains(card.getDisplayName(), expected)
                || contains(card.getUsername(), expected)
                || contains(card.getStudentId(), expected)
                || contains(card.getEmail(), expected)
                || contains(card.getProgramme(), expected)
                || contains(card.getSkills(), expected)
                || contains(card.getAvailability(), expected);
    }

    private static boolean matchesText(String source, String expected) {
        if (!hasText(expected)) {
            return true;
        }
        return contains(source, expected.trim().toLowerCase(Locale.ROOT));
    }

    private static boolean contains(String source, String expectedLowerCase) {
        return hasText(source) && source.trim().toLowerCase(Locale.ROOT).contains(expectedLowerCase);
    }

    private static boolean isTaRole(String role) {
        return hasText(role) && "TA".equalsIgnoreCase(role.trim());
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (hasText(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private static LinkedHashMap<String, String> parseSkillMap(String rawSkills) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        if (!hasText(rawSkills)) {
            return result;
        }

        for (String token : splitSkills(rawSkills)) {
            String cleaned = cleanSkillToken(token);
            if (!hasText(cleaned)) {
                continue;
            }
            String normalized = normalizeText(cleaned);
            if (!result.containsKey(normalized)) {
                result.put(normalized, cleaned);
            }
        }
        return result;
    }

    private static Set<String> parseSkillSet(String rawSkills) {
        Set<String> result = new LinkedHashSet<>();
        if (!hasText(rawSkills)) {
            return result;
        }

        for (String token : splitSkills(rawSkills)) {
            String cleaned = cleanSkillToken(token);
            if (hasText(cleaned)) {
                result.add(normalizeText(cleaned));
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

    private static String normalizeText(String value) {
        if (!hasText(value)) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeKey(String value) {
        if (!hasText(value)) {
            return "";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
