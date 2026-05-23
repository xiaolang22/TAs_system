package com.group19.dto;

public class MoTaCandidateCard {
    private String displayName;
    private String username;
    private String studentId;
    private String email;
    private String programme;
    private String skills;
    private String experience;
    private String availability;
    private String avatarPath;
    private String avatarUrl;
    private String cvFilePath;
    private String cvUrl;
    private String updatedAt;
    private boolean profileCompleted;
    private boolean resumeAvailable;
    private int matchScore;
    private String matchedSkillsText;
    private String missingSkillsText;
    private String matchNote;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCvFilePath() {
        return cvFilePath;
    }

    public void setCvFilePath(String cvFilePath) {
        this.cvFilePath = cvFilePath;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public boolean isResumeAvailable() {
        return resumeAvailable;
    }

    public void setResumeAvailable(boolean resumeAvailable) {
        this.resumeAvailable = resumeAvailable;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getMatchedSkillsText() {
        return matchedSkillsText;
    }

    public void setMatchedSkillsText(String matchedSkillsText) {
        this.matchedSkillsText = matchedSkillsText;
    }

    public String getMissingSkillsText() {
        return missingSkillsText;
    }

    public void setMissingSkillsText(String missingSkillsText) {
        this.missingSkillsText = missingSkillsText;
    }

    public String getMatchNote() {
        return matchNote;
    }

    public void setMatchNote(String matchNote) {
        this.matchNote = matchNote;
    }

    public String getAvatarInitial() {
        if (!hasText(displayName)) {
            return "T";
        }
        return displayName.trim().substring(0, 1);
    }

    public String getEmailDisplay() {
        return displayOrFallback(email, "Not provided");
    }

    public String getProgrammeDisplay() {
        return displayOrFallback(programme, "Not provided");
    }

    public String getSkillsDisplay() {
        return displayOrFallback(skills, "Not provided");
    }

    public String getAvailabilityDisplay() {
        return displayOrFallback(availability, "Not provided");
    }

    public String getExperienceDisplay() {
        return displayOrFallback(experience, "Not provided");
    }

    public String getUpdatedAtDisplay() {
        return displayOrFallback(updatedAt, "Profile not updated");
    }

    public String getMatchedSkillsDisplay() {
        return displayOrFallback(matchedSkillsText, "None");
    }

    public String getMissingSkillsDisplay() {
        return displayOrFallback(missingSkillsText, "None");
    }

    public boolean hasCv() {
        return resumeAvailable && hasText(cvFilePath) && hasText(cvUrl);
    }

    public String getArchiveStatusLabel() {
        if (profileCompleted && resumeAvailable) {
            return "Profile and resume completed";
        }
        if (profileCompleted) {
            return "Profile completed";
        }
        return "Profile incomplete";
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String displayOrFallback(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }
}
