package com.group19.dto;

/**
 * CV (Curriculum Vitae) extracted information data transfer object (DTO).
 * <p>
 * Encapsulates structured information automatically parsed and extracted from a CV file,
 * covering three dimensions: education background, skills list, and work experience.
 * All fields are immutable (final) and are initialised via the constructor with
 * automatic trimming of leading and trailing whitespace.
 * </p>
 *
 * @author Group 19
 */
public class CVExtractedInfo {

    /** Education background information extracted from the CV */
    private final String education;

    /** Skills list extracted from the CV */
    private final String skills;

    /** Work/project experience extracted from the CV */
    private final String experience;

    /**
     * Constructor, creates a CV extracted information object.
     * All fields are automatically trimmed of leading and trailing whitespace;
     * null values are converted to empty strings.
     *
     * @param education  education background
     * @param skills     skills list
     * @param experience work/project experience
     */
    public CVExtractedInfo(String education, String skills, String experience) {
        this.education = education == null ? "" : education.trim();
        this.skills = skills == null ? "" : skills.trim();
        this.experience = experience == null ? "" : experience.trim();
    }

    /** @return education background information */
    public String getEducation() {
        return education;
    }

    /** @return skills list */
    public String getSkills() {
        return skills;
    }

    /** @return work/project experience */
    public String getExperience() {
        return experience;
    }

    /**
     * Returns whether any information was extracted.
     *
     * @return true if at least one of education, skills, or experience is non-empty
     */
    public boolean hasAny() {
        return !education.isEmpty() || !skills.isEmpty() || !experience.isEmpty();
    }
}
