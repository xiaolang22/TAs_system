package com.group19.model;

/**
 * TA (Teaching Assistant) entity class, recording personal information and CV data
 * of TA students. Contains information such as name, student ID, email, programme,
 * skills, experience, and availability.
 *
 * @author Group19
 * @since 1.0
 */
public class TA {

    /** TA's name */
    private String name;

    /** Student ID */
    private String studentId;

    /** Email address */
    private String email;

    /** Programme of study */
    private String programme;

    /** Skills list (comma-separated) */
    private String skills;

    /** Description of relevant experience */
    private String experience;

    /** Availability description */
    private String availability;

    /** CV file path */
    private String cvFilePath;

    /** Last update time of the profile */
    private String updatedAt;

    /**
     * Default no-argument constructor.
     */
    public TA() {
    }

    /**
     * Creates a TA object with core information.
     *
     * @param name         TA's name
     * @param studentId    Student ID
     * @param email        Email address
     * @param programme    Programme of study
     * @param skills       Skills list
     * @param availability Availability
     */
    public TA(String name, String studentId, String email, String programme, String skills, String availability) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.programme = programme;
        this.skills = skills;
        this.availability = availability;
    }

    /** @return TA's name */
    public String getName() { return name; }
    /** @param name TA's name */
    public void setName(String name) { this.name = name; }

    /** @return Student ID */
    public String getStudentId() { return studentId; }
    /** @param studentId Student ID */
    public void setStudentId(String studentId) { this.studentId = studentId; }

    /** @return Email address */
    public String getEmail() { return email; }
    /** @param email Email address */
    public void setEmail(String email) { this.email = email; }

    /** @return Programme of study */
    public String getProgramme() { return programme; }
    /** @param programme Programme of study */
    public void setProgramme(String programme) { this.programme = programme; }

    /** @return Skills list */
    public String getSkills() { return skills; }
    /** @param skills Skills list */
    public void setSkills(String skills) { this.skills = skills; }

    /** @return Availability description */
    public String getAvailability() { return availability; }
    /** @param availability Availability description */
    public void setAvailability(String availability) { this.availability = availability; }

    /** @return Description of relevant experience */
    public String getExperience() { return experience; }
    /** @param experience Description of relevant experience */
    public void setExperience(String experience) { this.experience = experience; }

    /** @return CV file path */
    public String getCvFilePath() { return cvFilePath; }
    /** @param cvFilePath CV file path */
    public void setCvFilePath(String cvFilePath) { this.cvFilePath = cvFilePath; }

    /** @return Last update time of the profile */
    public String getUpdatedAt() { return updatedAt; }
    /** @param updatedAt Last update time of the profile */
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
