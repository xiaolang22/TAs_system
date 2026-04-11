package com.group19.service;

import com.group19.dto.ParsedCVData;
import com.group19.dto.ServiceResult;
import jakarta.servlet.http.Part;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CVParseService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile(
            "\\b(\\d{8,10})\\b");

    private static final List<String> PROGRAMME_KEYWORDS = List.of(
            "BSc", "MSc", "PhD", "Bachelor", "Master", "Doctor",
            "Computer Science", "Software Engineering", "Data Science",
            "Information Technology", "Electrical Engineering", "Mechanical Engineering");

    private static final List<String> SKILL_KEYWORDS = List.of(
            "Java", "Python", "C++", "JavaScript", "TypeScript", "React", "Vue", "Angular",
            "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis",
            "Git", "Docker", "Kubernetes", "AWS", "Azure", "Linux", "Windows",
            "Machine Learning", "Deep Learning", "Data Analysis", "Statistics");

    public ServiceResult<ParsedCVData> parseCV(Part cvPart) {
        if (cvPart == null || cvPart.getSize() <= 0) {
            return ServiceResult.failure("Please choose a CV file to parse.");
        }

        String fileName = cvPart.getSubmittedFileName();
        if (fileName == null) {
            return ServiceResult.failure("Invalid file name.");
        }

        String lowerFileName = fileName.toLowerCase();
        if (!lowerFileName.endsWith(".pdf")) {
            return ServiceResult.failure("Only PDF files are supported. Please upload a PDF file.");
        }

        try {
            String text = extractTextFromPDF(cvPart);
            ParsedCVData parsedData = parseText(text);
            return ServiceResult.success(parsedData, "CV parsed successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to read CV file: " + e.getMessage());
        }
    }

    private String extractTextFromPDF(Part part) throws IOException {
        try (InputStream is = part.getInputStream();
             PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private ParsedCVData parseText(String text) {
        ParsedCVData data = new ParsedCVData();

        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        if (emailMatcher.find()) {
            data.setEmail(emailMatcher.group());
        }

        Matcher studentIdMatcher = STUDENT_ID_PATTERN.matcher(text);
        if (studentIdMatcher.find()) {
            data.setStudentId(studentIdMatcher.group(1));
        }

        data.setName(extractName(text));
        data.setProgramme(extractProgramme(text));
        data.setSkills(extractSkills(text));
        data.setAvailability(extractAvailability(text));

        return data;
    }

    private String extractName(String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 2 && trimmed.length() < 50
                    && !trimmed.contains("@")
                    && !trimmed.matches(".*\\d.*")
                    && trimmed.matches("^[A-Z][a-z]+(?:\\s[A-Z][a-z]+)+$")) {
                return trimmed;
            }
        }
        return null;
    }

    private String extractProgramme(String text) {
        String lowerText = text.toLowerCase();
        for (String keyword : PROGRAMME_KEYWORDS) {
            if (lowerText.contains(keyword.toLowerCase())) {
                Pattern pattern = Pattern.compile(
                        "(?i)(BSc|MSc|PhD|Bachelor|Master|Doctor)\\s+(?:of\\s+)?([A-Za-z\\s]+?)(?:\\s|\\n|,|$)");
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    return matcher.group().trim();
                }
                return keyword;
            }
        }
        return null;
    }

    private String extractSkills(String text) {
        List<String> foundSkills = new ArrayList<>();
        String lowerText = text.toLowerCase();
        for (String skill : SKILL_KEYWORDS) {
            if (lowerText.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }
        if (foundSkills.isEmpty()) {
            return null;
        }
        return String.join(", ", foundSkills);
    }

    private String extractAvailability(String text) {
        String lowerText = text.toLowerCase();
        if (lowerText.contains("available") || lowerText.contains("availability")) {
            Pattern pattern = Pattern.compile(
                    "(?i)(available|availability).{0,100}(?:\\.|\\n|$)",
                    Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group().trim();
            }
        }
        return null;
    }
}
