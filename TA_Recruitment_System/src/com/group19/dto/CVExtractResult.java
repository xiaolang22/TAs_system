package com.group19.dto;

public class CVExtractResult {
    private final String programme;
    private final String skills;
    private final String experience;
    private final String extractedText;

    public CVExtractResult(String programme, String skills, String experience, String extractedText) {
        this.programme = programme;
        this.skills = skills;
        this.experience = experience;
        this.extractedText = extractedText;
    }

    public String getProgramme() {
        return programme;
    }

    public String getSkills() {
        return skills;
    }

    public String getExperience() {
        return experience;
    }

    public String getExtractedText() {
        return extractedText;
    }
}
