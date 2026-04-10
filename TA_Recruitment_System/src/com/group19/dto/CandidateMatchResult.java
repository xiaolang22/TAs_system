package com.group19.dto;

import java.util.List;

public class CandidateMatchResult {
    private final String candidateName;
    private final String studentId;
    private final String candidateSkillsText;
    private final int matchScore;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String note;

    public CandidateMatchResult(
            String candidateName,
            String studentId,
            String candidateSkillsText,
            int matchScore,
            List<String> matchedSkills,
            List<String> missingSkills,
            String note) {
        this.candidateName = candidateName;
        this.studentId = studentId;
        this.candidateSkillsText = candidateSkillsText;
        this.matchScore = matchScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.note = note;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCandidateSkillsText() {
        return candidateSkillsText;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public String getNote() {
        return note;
    }
}
