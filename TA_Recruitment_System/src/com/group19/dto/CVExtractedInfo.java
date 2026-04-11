package com.group19.dto;

public class CVExtractedInfo {
    private final String education;
    private final String skills;
    private final String experience;

    public CVExtractedInfo(String education, String skills, String experience) {
        this.education = education == null ? "" : education.trim();
        this.skills = skills == null ? "" : skills.trim();
        this.experience = experience == null ? "" : experience.trim();
    }

    public String getEducation() {
        return education;
    }

    public String getSkills() {
        return skills;
    }

    public String getExperience() {
        return experience;
    }

    public boolean hasAny() {
        return !education.isEmpty() || !skills.isEmpty() || !experience.isEmpty();
    }
}
