package com.group19.util;

import com.group19.dto.CVExtractedInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class CVParserUtil {
    private static final int MAX_SECTION_LENGTH = 600;

    private static final List<String> EDUCATION_HEADINGS = List.of(
            "education",
            "educational background",
            "学历",
            "教育背景",
            "education background");
    private static final List<String> SKILL_HEADINGS = List.of(
            "skills",
            "skill",
            "technical skills",
            "核心技能",
            "技能",
            "技术栈");
    private static final List<String> EXPERIENCE_HEADINGS = List.of(
            "experience",
            "work experience",
            "project experience",
            "internship",
            "经历",
            "工作经历",
            "项目经历");

    private static final Pattern TOKEN_SPLIT = Pattern.compile("[,;|/、]");

    private CVParserUtil() {
    }

    public static CVExtractedInfo extract(Path cvFilePath) {
        if (cvFilePath == null || !Files.exists(cvFilePath)) {
            return new CVExtractedInfo("", "", "");
        }

        try {
            String extension = extensionOf(cvFilePath.getFileName().toString());
            String rawText = "docx".equals(extension)
                    ? readDocxText(cvFilePath)
                    : readGenericText(cvFilePath);
            return parseFromText(rawText);
        } catch (IOException e) {
            return new CVExtractedInfo("", "", "");
        }
    }

    private static CVExtractedInfo parseFromText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return new CVExtractedInfo("", "", "");
        }

        List<String> lines = cleanedLines(rawText);
        if (lines.isEmpty()) {
            return new CVExtractedInfo("", "", "");
        }

        List<String> allHeadings = new ArrayList<>();
        allHeadings.addAll(EDUCATION_HEADINGS);
        allHeadings.addAll(SKILL_HEADINGS);
        allHeadings.addAll(EXPERIENCE_HEADINGS);

        String education = extractSection(lines, EDUCATION_HEADINGS, allHeadings);
        String skills = extractSection(lines, SKILL_HEADINGS, allHeadings);
        String experience = extractSection(lines, EXPERIENCE_HEADINGS, allHeadings);

        if (skills.isEmpty()) {
            skills = extractSkillsFallback(lines);
        }
        if (education.isEmpty()) {
            education = extractFirstMatchingLine(lines, "bachelor", "master", "phd", "university", "college", "本科", "硕士", "博士");
        }
        if (experience.isEmpty()) {
            experience = extractFirstMatchingLine(lines, "intern", "assistant", "project", "experience", "实习", "项目", "经历");
        }

        return new CVExtractedInfo(trimToLimit(education), trimToLimit(skills), trimToLimit(experience));
    }

    private static String extractSection(List<String> lines, List<String> sectionHeadings, List<String> allHeadings) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String normalizedLine = normalizeHeading(line);
            if (normalizedLine.isEmpty()) {
                continue;
            }

            for (String heading : sectionHeadings) {
                String normalizedHeading = normalizeHeading(heading);
                if (matchesHeadingLine(normalizedLine, normalizedHeading)) {
                    String inlineValue = extractInlineValue(line);
                    String follow = collectFollowingLines(lines, i + 1, allHeadings);
                    if (!inlineValue.isEmpty() && !follow.isEmpty()) {
                        return inlineValue + "; " + follow;
                    }
                    if (!inlineValue.isEmpty()) {
                        return inlineValue;
                    }
                    if (!follow.isEmpty()) {
                        return follow;
                    }
                }
            }
        }
        return "";
    }

    private static String collectFollowingLines(List<String> lines, int start, List<String> allHeadings) {
        StringBuilder builder = new StringBuilder();
        int usedLineCount = 0;
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                if (usedLineCount > 0) {
                    break;
                }
                continue;
            }
            if (isAnyHeading(line, allHeadings)) {
                break;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(line.trim());
            usedLineCount++;
            if (usedLineCount >= 3 || builder.length() >= MAX_SECTION_LENGTH) {
                break;
            }
        }
        return builder.toString().trim();
    }

    private static boolean isAnyHeading(String line, List<String> headings) {
        String normalizedLine = normalizeHeading(line);
        for (String heading : headings) {
            if (matchesHeadingLine(normalizedLine, normalizeHeading(heading))) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesHeadingLine(String normalizedLine, String normalizedHeading) {
        if (normalizedHeading.isEmpty()) {
            return false;
        }
        if (normalizedLine.equals(normalizedHeading)) {
            return true;
        }
        return normalizedLine.startsWith(normalizedHeading + ":")
                || normalizedLine.startsWith(normalizedHeading + "：")
                || normalizedLine.startsWith(normalizedHeading + "-")
                || normalizedLine.startsWith(normalizedHeading + " ");
    }

    private static String extractInlineValue(String line) {
        if (line == null) {
            return "";
        }
        int colon = line.indexOf(':');
        if (colon < 0) {
            colon = line.indexOf('：');
        }
        if (colon < 0 || colon == line.length() - 1) {
            return "";
        }
        return line.substring(colon + 1).trim();
    }

    private static String extractSkillsFallback(List<String> lines) {
        Set<String> candidates = new LinkedHashSet<>();
        List<String> keywords = Arrays.asList(
                "java", "python", "c++", "c#", "sql", "javascript", "html", "css",
                "excel", "communication", "teaching", "analysis", "机器学习", "数据分析");
        for (String line : lines) {
            String lower = line.toLowerCase(Locale.ROOT);
            for (String key : keywords) {
                if (lower.contains(key.toLowerCase(Locale.ROOT))) {
                    for (String token : TOKEN_SPLIT.split(line)) {
                        String cleaned = token.trim();
                        if (cleaned.length() >= 2 && cleaned.length() <= 40) {
                            candidates.add(cleaned);
                        }
                    }
                    break;
                }
            }
            if (candidates.size() >= 8) {
                break;
            }
        }
        return String.join(", ", candidates);
    }

    private static String extractFirstMatchingLine(List<String> lines, String... keywords) {
        for (String line : lines) {
            String lower = line.toLowerCase(Locale.ROOT);
            for (String key : keywords) {
                if (lower.contains(key.toLowerCase(Locale.ROOT))) {
                    return line;
                }
            }
        }
        return "";
    }

    private static List<String> cleanedLines(String content) {
        String normalized = content
                .replace("\r\n", "\n")
                .replace('\r', '\n');
        String[] parts = normalized.split("\n");
        List<String> lines = new ArrayList<>(parts.length);
        for (String part : parts) {
            String line = part.replaceAll("\\s+", " ").trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static String readDocxText(Path filePath) throws IOException {
        try (InputStream in = Files.newInputStream(filePath); ZipInputStream zip = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName())) {
                    String xml = readAll(zip, StandardCharsets.UTF_8);
                    String text = xml
                            .replaceAll("<w:tab\\s*/>", "\t")
                            .replaceAll("<w:br\\s*/>", "\n")
                            .replaceAll("</w:p>", "\n")
                            .replaceAll("<[^>]+>", " ");
                    return unescapeXml(text);
                }
            }
        }
        return "";
    }

    private static String readGenericText(Path filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(filePath);
        String utf8 = toReadableText(bytes, StandardCharsets.UTF_8);
        if (countLetters(utf8) >= 30) {
            return utf8;
        }
        try {
            String gb = toReadableText(bytes, Charset.forName("GB18030"));
            if (countLetters(gb) > countLetters(utf8)) {
                return gb;
            }
        } catch (Exception ignored) {
            // fallback to UTF-8 result
        }
        return utf8;
    }

    private static String toReadableText(byte[] bytes, Charset charset) {
        String raw = new String(bytes, charset);
        StringBuilder out = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (isReadable(c)) {
                out.append(c);
            } else {
                out.append(' ');
            }
        }
        return out.toString();
    }

    private static boolean isReadable(char c) {
        return Character.isLetterOrDigit(c)
                || Character.isWhitespace(c)
                || ",.;:!?()[]{}<>@#+-/&_".indexOf(c) >= 0
                || (c >= 0x4E00 && c <= 0x9FFF);
    }

    private static int countLetters(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetterOrDigit(text.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    private static String readAll(InputStream in, Charset charset) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toString(charset);
    }

    private static String trimToLimit(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= MAX_SECTION_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, MAX_SECTION_LENGTH).trim();
    }

    private static String normalizeHeading(String text) {
        if (text == null) {
            return "";
        }
        String cleaned = text.toLowerCase(Locale.ROOT).trim();
        cleaned = cleaned.replaceAll("[\\t\\r\\n]+", " ");
        cleaned = cleaned.replaceAll("\\s+", " ");
        return cleaned;
    }

    private static String unescapeXml(String value) {
        return value
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&amp;", "&");
    }

    private static String extensionOf(String fileName) {
        int dot = fileName == null ? -1 : fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
