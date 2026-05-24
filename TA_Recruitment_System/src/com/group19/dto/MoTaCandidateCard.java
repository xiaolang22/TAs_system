package com.group19.dto;

/**
 * MO-facing TA candidate card data transfer object (DTO).
 * <p>
 * Encapsulates all information required for each candidate card when an MO views
 * the TA candidate list. Includes personal information (name, student ID, email,
 * programme), skills and experience, avatar and CV file paths, match score and
 * matched skill details, as well as various derived methods for front-end display
 * (e.g., avatar initial, display fallback logic).
 * </p>
 *
 * @author Group 19
 */
public class MoTaCandidateCard {

    /** Display name of the candidate */
    private String displayName;

    /** Username of the candidate */
    private String username;

    /** Student ID of the candidate */
    private String studentId;

    /** Email of the candidate */
    private String email;

    /** Programme of the candidate */
    private String programme;

    /** Skill description of the candidate */
    private String skills;

    /** Experience description of the candidate */
    private String experience;

    /** Availability description of the candidate */
    private String availability;

    /** Avatar file path */
    private String avatarPath;

    /** Avatar access URL */
    private String avatarUrl;

    /** CV file path */
    private String cvFilePath;

    /** CV access URL */
    private String cvUrl;

    /** Last profile update time */
    private String updatedAt;

    /** Whether the personal profile is complete */
    private boolean profileCompleted;

    /** Whether a CV has been uploaded */
    private boolean resumeAvailable;

    /** Skill match score */
    private int matchScore;

    /** Text description of matched skills */
    private String matchedSkillsText;

    /** Text description of missing skills */
    private String missingSkillsText;

    /** Match note / explanation */
    private String matchNote;

    /** @return display name of the candidate */
    public String getDisplayName() {
        return displayName;
    }

    /** @param displayName display name of the candidate */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /** @return username of the candidate */
    public String getUsername() {
        return username;
    }

    /** @param username username of the candidate */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return student ID of the candidate */
    public String getStudentId() {
        return studentId;
    }

    /** @param studentId student ID of the candidate */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /** @return email of the candidate */
    public String getEmail() {
        return email;
    }

    /** @param email email of the candidate */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return programme of the candidate */
    public String getProgramme() {
        return programme;
    }

    /** @param programme programme of the candidate */
    public void setProgramme(String programme) {
        this.programme = programme;
    }

    /** @return skill description of the candidate */
    public String getSkills() {
        return skills;
    }

    /** @param skills skill description of the candidate */
    public void setSkills(String skills) {
        this.skills = skills;
    }

    /** @return experience description of the candidate */
    public String getExperience() {
        return experience;
    }

    /** @param experience experience description of the candidate */
    public void setExperience(String experience) {
        this.experience = experience;
    }

    /** @return availability description of the candidate */
    public String getAvailability() {
        return availability;
    }

    /** @param availability availability description of the candidate */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /** @return avatar file path */
    public String getAvatarPath() {
        return avatarPath;
    }

    /** @param avatarPath avatar file path */
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    /** @return avatar access URL */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /** @param avatarUrl avatar access URL */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /** @return CV file path */
    public String getCvFilePath() {
        return cvFilePath;
    }

    /** @param cvFilePath CV file path */
    public void setCvFilePath(String cvFilePath) {
        this.cvFilePath = cvFilePath;
    }

    /** @return CV access URL */
    public String getCvUrl() {
        return cvUrl;
    }

    /** @param cvUrl CV access URL */
    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    /** @return last profile update time */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /** @param updatedAt last profile update time */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** @return whether the personal profile is complete */
    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    /** @param profileCompleted whether the personal profile is complete */
    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    /** @return whether a CV has been uploaded */
    public boolean isResumeAvailable() {
        return resumeAvailable;
    }

    /** @param resumeAvailable whether a CV has been uploaded */
    public void setResumeAvailable(boolean resumeAvailable) {
        this.resumeAvailable = resumeAvailable;
    }

    /** @return skill match score */
    public int getMatchScore() {
        return matchScore;
    }

    /** @param matchScore skill match score */
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    /** @return text description of matched skills */
    public String getMatchedSkillsText() {
        return matchedSkillsText;
    }

    /** @param matchedSkillsText text description of matched skills */
    public void setMatchedSkillsText(String matchedSkillsText) {
        this.matchedSkillsText = matchedSkillsText;
    }

    /** @return text description of missing skills */
    public String getMissingSkillsText() {
        return missingSkillsText;
    }

    /** @param missingSkillsText text description of missing skills */
    public void setMissingSkillsText(String missingSkillsText) {
        this.missingSkillsText = missingSkillsText;
    }

    /** @return match note */
    public String getMatchNote() {
        return matchNote;
    }

    /** @param matchNote match note */
    public void setMatchNote(String matchNote) {
        this.matchNote = matchNote;
    }

    /**
     * Returns the initial letter for avatar display.
     * Defaults to "T" if the display name is empty.
     *
     * @return the first character of the display name
     */
    public String getAvatarInitial() {
        if (!hasText(displayName)) {
            return "T";
        }
        return displayName.trim().substring(0, 1);
    }

    /**
     * Returns the email display text, with a fallback value when empty.
     *
     * @return the email or "Not provided"
     */
    public String getEmailDisplay() {
        return displayOrFallback(email, "Not provided");
    }

    /**
     * Returns the programme display text, with a fallback value when empty.
     *
     * @return the programme or "Not provided"
     */
    public String getProgrammeDisplay() {
        return displayOrFallback(programme, "Not provided");
    }

    /**
     * Returns the skills display text, with a fallback value when empty.
     *
     * @return the skills or "Not provided"
     */
    public String getSkillsDisplay() {
        return displayOrFallback(skills, "Not provided");
    }

    /**
     * Returns the availability display text, with a fallback value when empty.
     *
     * @return the availability or "Not provided"
     */
    public String getAvailabilityDisplay() {
        return displayOrFallback(availability, "Not provided");
    }

    /**
     * Returns the experience display text, with a fallback value when empty.
     *
     * @return the experience or "Not provided"
     */
    public String getExperienceDisplay() {
        return displayOrFallback(experience, "Not provided");
    }

    /**
     * Returns the updated-at display text, with a fallback value when empty.
     *
     * @return the update time or "Profile not updated"
     */
    public String getUpdatedAtDisplay() {
        return displayOrFallback(updatedAt, "Profile not updated");
    }

    /**
     * Returns the matched skills display text, with a fallback value when empty.
     *
     * @return the matched skills or "None"
     */
    public String getMatchedSkillsDisplay() {
        return displayOrFallback(matchedSkillsText, "None");
    }

    /**
     * Returns the missing skills display text, with a fallback value when empty.
     *
     * @return the missing skills or "None"
     */
    public String getMissingSkillsDisplay() {
        return displayOrFallback(missingSkillsText, "None");
    }

    /**
     * Checks whether the candidate has a CV file.
     *
     * @return true if a CV is available and has both a file path and a URL
     */
    public boolean hasCv() {
        return resumeAvailable && hasText(cvFilePath) && hasText(cvUrl);
    }

    /**
     * Returns a display label for the candidate's profile completion status.
     *
     * @return a profile status label describing the level of completion
     */
    public String getArchiveStatusLabel() {
        if (profileCompleted && resumeAvailable) {
            return "Profile and resume completed";
        }
        if (profileCompleted) {
            return "Profile completed";
        }
        return "Profile incomplete";
    }

    /**
     * Checks whether a string is non-empty.
     *
     * @param value the string to check
     * @return true if non-null and non-blank
     */
    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Returns the valid value or a fallback value.
     *
     * @param value    the original value
     * @param fallback the fallback value
     * @return the trimmed original value if non-empty, otherwise the fallback
     */
    private static String displayOrFallback(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }
}
