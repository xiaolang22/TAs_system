package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.TA;

/**
 * Unit tests for the TA model class.
 * Tests default constructor, parameterised constructor (6 args), and all 9 getter/setter pairs.
 *
 * @author Group19
 * @since 1.0
 */
public class TATest extends TestRunner {

    public void testDefaultConstructorInitialisesAllFieldsToNull() {
        TA ta = new TA();
        assertNotNull("Default constructor should create a non-null TA object", ta);
        assertNull("name should be null after default constructor", ta.getName());
        assertNull("studentId should be null after default constructor", ta.getStudentId());
        assertNull("email should be null after default constructor", ta.getEmail());
        assertNull("programme should be null after default constructor", ta.getProgramme());
        assertNull("skills should be null after default constructor", ta.getSkills());
        assertNull("experience should be null after default constructor", ta.getExperience());
        assertNull("availability should be null after default constructor", ta.getAvailability());
        assertNull("cvFilePath should be null after default constructor", ta.getCvFilePath());
        assertNull("updatedAt should be null after default constructor", ta.getUpdatedAt());
    }

    public void testParameterisedConstructorSetsSixCoreFields() {
        TA ta = new TA("Alice Johnson", "STU001", "zhangsan@university.edu", "Software Engineering", "Java, Python", "Mon-Fri mornings");
        assertNotNull("Parameterised constructor should create a non-null TA object", ta);
        assertEquals("Alice Johnson", ta.getName());
        assertEquals("STU001", ta.getStudentId());
        assertEquals("zhangsan@university.edu", ta.getEmail());
        assertEquals("Software Engineering", ta.getProgramme());
        assertEquals("Java, Python", ta.getSkills());
        assertEquals("Mon-Fri mornings", ta.getAvailability());
        // Fields not set by parameterised constructor should remain null
        assertNull("experience should be null after parameterised constructor", ta.getExperience());
        assertNull("cvFilePath should be null after parameterised constructor", ta.getCvFilePath());
        assertNull("updatedAt should be null after parameterised constructor", ta.getUpdatedAt());
    }

    public void testSetAndGetName() {
        TA ta = new TA();
        ta.setName("Bob Smith");
        assertEquals("Bob Smith", ta.getName());
    }

    public void testSetAndGetStudentId() {
        TA ta = new TA();
        ta.setStudentId("STU002");
        assertEquals("STU002", ta.getStudentId());
    }

    public void testSetAndGetEmail() {
        TA ta = new TA();
        ta.setEmail("lisi@university.edu");
        assertEquals("lisi@university.edu", ta.getEmail());
    }

    public void testSetAndGetProgramme() {
        TA ta = new TA();
        ta.setProgramme("Computer Science");
        assertEquals("Computer Science", ta.getProgramme());
    }

    public void testSetAndGetSkills() {
        TA ta = new TA();
        ta.setSkills("C++, Machine Learning");
        assertEquals("C++, Machine Learning", ta.getSkills());
    }

    public void testSetAndGetExperience() {
        TA ta = new TA();
        ta.setExperience("Served as a Data Structures teaching assistant in 2023");
        assertEquals("Served as a Data Structures teaching assistant in 2023", ta.getExperience());
    }

    public void testSetAndGetAvailability() {
        TA ta = new TA();
        ta.setAvailability("Tuesday and Thursday afternoons");
        assertEquals("Tuesday and Thursday afternoons", ta.getAvailability());
    }

    public void testSetAndGetCvFilePath() {
        TA ta = new TA();
        ta.setCvFilePath("/uploads/ta_cv.pdf");
        assertEquals("/uploads/ta_cv.pdf", ta.getCvFilePath());
    }

    public void testSetAndGetUpdatedAt() {
        TA ta = new TA();
        ta.setUpdatedAt("2024-03-10T15:00:00");
        assertEquals("2024-03-10T15:00:00", ta.getUpdatedAt());
    }

    public void testAllFieldsSetAndGetConsistently() {
        TA ta = new TA();
        ta.setName("Charlie Brown");
        ta.setStudentId("STU003");
        ta.setEmail("wangwu@university.edu");
        ta.setProgramme("Data Science");
        ta.setSkills("R, SQL, Python");
        ta.setExperience("Two years of teaching support experience");
        ta.setAvailability("All day Wednesday");
        ta.setCvFilePath("/uploads/wangwu_cv.pdf");
        ta.setUpdatedAt("2024-04-01T10:00:00");

        assertEquals("Charlie Brown", ta.getName());
        assertEquals("STU003", ta.getStudentId());
        assertEquals("wangwu@university.edu", ta.getEmail());
        assertEquals("Data Science", ta.getProgramme());
        assertEquals("R, SQL, Python", ta.getSkills());
        assertEquals("Two years of teaching support experience", ta.getExperience());
        assertEquals("All day Wednesday", ta.getAvailability());
        assertEquals("/uploads/wangwu_cv.pdf", ta.getCvFilePath());
        assertEquals("2024-04-01T10:00:00", ta.getUpdatedAt());
    }

    public static void main(String[] args) {
        new TATest().runTestsAndExit();
    }
}
