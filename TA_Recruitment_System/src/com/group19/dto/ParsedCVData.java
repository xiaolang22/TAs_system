package com.group19.dto;

/**
 * Parsed CV data transfer object (DTO).
 * <p>
 * Encapsulates a TA's personal information automatically parsed and extracted from an
 * uploaded CV file (PDF/DOCX), including name, email, student ID, programme, skills,
 * experience, and availability. Used for the CV auto-fill feature to reduce manual
 * data entry by TAs.
 * </p>
 *
 * @author Group 19
 */
public class ParsedCVData {

    /** Parsed name */
    private String name;

    /** Parsed email */
    private String email;

    /** Parsed student ID */
    private String studentId;

    /** Parsed programme */
    private String programme;

    /** Parsed skills list */
    private String skills;

    /** Parsed work/project experience */
    private String experience;

    /** Parsed availability */
    private String availability;

    /**
     * Default no-argument constructor.
     */
    public ParsedCVData() {
    }

    /** @return parsed name */
    public String getName() {
        return name;
    }

    /** @param name parsed name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return parsed email */
    public String getEmail() {
        return email;
    }

    /** @param email parsed email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return parsed student ID */
    public String getStudentId() {
        return studentId;
    }

    /** @param studentId parsed student ID */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /** @return parsed programme */
    public String getProgramme() {
        return programme;
    }

    /** @param programme parsed programme */
    public void setProgramme(String programme) {
        this.programme = programme;
    }

    /** @return parsed skills list */
    public String getSkills() {
        return skills;
    }

    /** @param skills parsed skills list */
    public void setSkills(String skills) {
        this.skills = skills;
    }

    /** @return parsed work/project experience */
    public String getExperience() {
        return experience;
    }

    /** @param experience parsed work/project experience */
    public void setExperience(String experience) {
        this.experience = experience;
    }

    /** @return parsed availability */
    public String getAvailability() {
        return availability;
    }

    /** @param availability parsed availability */
    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
