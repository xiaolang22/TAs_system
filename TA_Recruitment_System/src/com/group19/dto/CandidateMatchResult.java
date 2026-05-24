package com.group19.dto;

import java.util.List;

/**
 * Candidate match result data transfer object (DTO).
 * <p>
 * Encapsulates the skill match analysis result between a TA candidate and a specific
 * job position's requirements. Includes basic candidate information, match score,
 * list of matched skills, list of missing skills, and a match note.
 * All fields are immutable (final).
 * </p>
 *
 * @author Group 19
 */
public class CandidateMatchResult {

    /** Candidate name */
    private final String candidateName;

    /** Candidate student ID */
    private final String studentId;

    /** Text description of the candidate's skills */
    private final String candidateSkillsText;

    /** Skill match score */
    private final int matchScore;

    /** List of skills that match the job requirements */
    private final List<String> matchedSkills;

    /** List of skills required by the job that the candidate lacks */
    private final List<String> missingSkills;

    /** Match note / explanation */
    private final String note;

    /**
     * Constructor, creates a candidate match result object.
     *
     * @param candidateName       candidate name
     * @param studentId           candidate student ID
     * @param candidateSkillsText text description of the candidate's skills
     * @param matchScore          skill match score
     * @param matchedSkills       list of matched skills
     * @param missingSkills       list of missing skills
     * @param note                match note
     */
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

    /** @return candidate name */
    public String getCandidateName() {
        return candidateName;
    }

    /** @return candidate student ID */
    public String getStudentId() {
        return studentId;
    }

    /** @return text description of the candidate's skills */
    public String getCandidateSkillsText() {
        return candidateSkillsText;
    }

    /** @return skill match score */
    public int getMatchScore() {
        return matchScore;
    }

    /** @return list of matched skills */
    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    /** @return list of missing skills */
    public List<String> getMissingSkills() {
        return missingSkills;
    }

    /** @return match note */
    public String getNote() {
        return note;
    }
}
