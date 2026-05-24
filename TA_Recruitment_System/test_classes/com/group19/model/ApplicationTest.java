package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.Application;

/**
 * Unit tests for the Application model class.
 * Tests default constructor and all 9 getter/setter pairs.
 *
 * @author Group19
 * @since 1.0
 */
public class ApplicationTest extends TestRunner {

    public void testDefaultConstructorInitialisesAllFieldsToNull() {
        Application app = new Application();
        assertNotNull("Default constructor should create a non-null Application object", app);
        assertNull("applicationId should be null after default constructor", app.getApplicationId());
        assertNull("jobId should be null after default constructor", app.getJobId());
        assertNull("taStudentId should be null after default constructor", app.getTaStudentId());
        assertNull("taName should be null after default constructor", app.getTaName());
        assertNull("cvFilePath should be null after default constructor", app.getCvFilePath());
        assertNull("status should be null after default constructor", app.getStatus());
        assertNull("submittedAt should be null after default constructor", app.getSubmittedAt());
        assertNull("updatedAt should be null after default constructor", app.getUpdatedAt());
        assertNull("decisionNote should be null after default constructor", app.getDecisionNote());
    }

    public void testSetAndGetApplicationId() {
        Application app = new Application();
        app.setApplicationId("APP001");
        assertEquals("APP001", app.getApplicationId());
    }

    public void testSetAndGetJobId() {
        Application app = new Application();
        app.setJobId("JOB001");
        assertEquals("JOB001", app.getJobId());
    }

    public void testSetAndGetTaStudentId() {
        Application app = new Application();
        app.setTaStudentId("STU001");
        assertEquals("STU001", app.getTaStudentId());
    }

    public void testSetAndGetTaName() {
        Application app = new Application();
        app.setTaName("Alice Johnson");
        assertEquals("Alice Johnson", app.getTaName());
    }

    public void testSetAndGetCvFilePath() {
        Application app = new Application();
        app.setCvFilePath("/uploads/cv.pdf");
        assertEquals("/uploads/cv.pdf", app.getCvFilePath());
    }

    public void testSetAndGetStatus() {
        Application app = new Application();
        app.setStatus("submitted");
        assertEquals("submitted", app.getStatus());
    }

    public void testSetAndGetSubmittedAt() {
        Application app = new Application();
        app.setSubmittedAt("2024-01-15T10:30:00");
        assertEquals("2024-01-15T10:30:00", app.getSubmittedAt());
    }

    public void testSetAndGetUpdatedAt() {
        Application app = new Application();
        app.setUpdatedAt("2024-01-16T14:00:00");
        assertEquals("2024-01-16T14:00:00", app.getUpdatedAt());
    }

    public void testSetAndGetDecisionNote() {
        Application app = new Application();
        app.setDecisionNote("Approved based on qualifications");
        assertEquals("Approved based on qualifications", app.getDecisionNote());
    }

    public void testAllFieldsSetAndGetConsistently() {
        Application app = new Application();
        app.setApplicationId("APP002");
        app.setJobId("JOB002");
        app.setTaStudentId("STU002");
        app.setTaName("Bob Smith");
        app.setCvFilePath("/uploads/cv2.pdf");
        app.setStatus("under_review");
        app.setSubmittedAt("2024-02-01T09:00:00");
        app.setUpdatedAt("2024-02-02T11:00:00");
        app.setDecisionNote("Pending review");

        assertEquals("APP002", app.getApplicationId());
        assertEquals("JOB002", app.getJobId());
        assertEquals("STU002", app.getTaStudentId());
        assertEquals("Bob Smith", app.getTaName());
        assertEquals("/uploads/cv2.pdf", app.getCvFilePath());
        assertEquals("under_review", app.getStatus());
        assertEquals("2024-02-01T09:00:00", app.getSubmittedAt());
        assertEquals("2024-02-02T11:00:00", app.getUpdatedAt());
        assertEquals("Pending review", app.getDecisionNote());
    }

    public static void main(String[] args) {
        new ApplicationTest().runTestsAndExit();
    }
}
