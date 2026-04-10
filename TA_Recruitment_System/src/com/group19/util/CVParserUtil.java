package com.group19.util;

import com.group19.dto.CVExtractResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public final class CVParserUtil {
    private CVParserUtil() {
    }

    public static CVExtractResult parse(String rawText) {
        String normalized = normalize(rawText);
        if (normalized.isEmpty()) {
            return new CVExtractResult("", "", "", "");
        }

        List<String> lines = splitLines(normalized);
        String programme = extractBySection(lines, "education");
        String skills = extractBySection(lines, "skills");
        String experience = extractBySection(lines, "experience");

        if (programme.isEmpty()) {
            programme = fallbackEducation(lines);
        }
        if (skills.isEmpty()) {
            skills = fallbackSkills(lines);
        }
        if (experience.isEmpty()) {
            experience = fallbackExperience(lines);
        }

        return new CVExtractResult(programme, skills, experience, normalized);
    }

    private static List<String> splitLines(String content) {
        String[] chunks = content.split("\n");
        List<String> lines = new ArrayList<>();
        for (String chunk : chunks) {
            String line = chunk == null ? "" : chunk.trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static String extractBySection(List<String> lines, String sectionType) {
        StringBuilder builder = new StringBuilder();
        boolean inSection = false;
        for (String line : lines) {
            if (isSectionHeader(line, sectionType)) {
                inSection = true;
                continue;
            }
            if (inSection && isAnySectionHeader(line)) {
                break;
            }
            if (inSection) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(line);
            }
        }
        return builder.toString().trim();
    }

    private static boolean isAnySectionHeader(String line) {
        return isSectionHeader(line, "education")
                || isSectionHeader(line, "skills")
                || isSectionHeader(line, "experience");
    }

    private static boolean isSectionHeader(String line, String sectionType) {
        String normalized = normalizeHeader(line);
        if (normalized.isEmpty()) {
            return false;
        }
        return switch (sectionType) {
            case "education" ->
                    normalized.equals("education")
                            || normalized.equals("academic background")
                            || normalized.equals("education background");
            case "skills" ->
                    normalized.equals("skills")
                            || normalized.equals("technical skills")
                            || normalized.equals("core skills");
            case "experience" ->
                    normalized.equals("experience")
                            || normalized.equals("work experience")
                            || normalized.equals("project experience")
                            || normalized.equals("projects");
            default -> false;
        };
    }

    private static String normalizeHeader(String line) {
        String normalized = line.toLowerCase(Locale.ROOT).trim();
        normalized = normalized.replace("：", ":");
        if (normalized.endsWith(":")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }
        return normalized;
    }

    private static String fallbackEducation(List<String> lines) {
        StringJoiner joiner = new StringJoiner("\n");
        for (String line : lines) {
            String lower = line.toLowerCase(Locale.ROOT);
            if (containsAny(lower, "university", "college", "bachelor", "master", "phd", "programme", "major")) {
                joiner.add(line);
            }
        }
        return joiner.toString();
    }

    private static String fallbackSkills(List<String> lines) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String line : lines) {
            String lower = line.toLowerCase(Locale.ROOT);
            if (containsAny(lower, "java", "python", "sql", "excel", "communication", "teaching", "data")
                    || line.contains(",")) {
                joiner.add(line);
            }
        }
        return joiner.toString();
    }

    private static String fallbackExperience(List<String> lines) {
        StringJoiner joiner = new StringJoiner("\n");
        for (String line : lines) {
            String lower = line.toLowerCase(Locale.ROOT);
            if (containsAny(lower, "intern", "assistant", "project", "experience", "worked", "developed", "202")) {
                joiner.add(line);
            }
        }
        return joiner.toString();
    }

    private static boolean containsAny(String content, String... keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\r", "").trim();
    }
}
