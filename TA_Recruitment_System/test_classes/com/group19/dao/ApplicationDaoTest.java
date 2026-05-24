package com.group19.dao;

import com.group19.TestRunner;
import com.group19.model.Application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for ApplicationDao. Uses temporary JSON files for data read/write verification.
 *
 * @author Group19
 * @since 1.0
 */
public class ApplicationDaoTest extends TestRunner {

    private Path tempFile;
    private ApplicationDao dao;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("test-applications-", ".json");
        Files.writeString(tempFile, "[]");
        dao = new ApplicationDao(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    // ---------- findAll ----------

    /** findAll() returns an empty list initially. */
    public void testFindAllReturnsEmptyListInitially() {
        List<Application> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertTrue("initial list should be empty", result.isEmpty());
    }

    // ---------- save ----------

    /** save() adds a new application; findAll() returns that record afterwards. */
    public void testSaveAddsApplication() {
        Application app = createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted");
        boolean saved = dao.save(app);
        assertTrue("save() should return true", saved);

        List<Application> all = dao.findAll();
        assertEquals("one record should exist after saving", 1, all.size());
        assertEquals("APP-001", all.get(0).getApplicationId());
    }

    /** After multiple save() calls, findAll() returns all records. */
    public void testFindAllReturnsSavedApplicationsAfterMultipleSaves() {
        dao.save(createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted"));
        dao.save(createApplication("APP-002", "JOB-002", "STU-002", "Bob Smith", "submitted"));
        dao.save(createApplication("APP-003", "JOB-001", "STU-003", "Charlie Brown", "under_review"));

        List<Application> all = dao.findAll();
        assertEquals("should have three records", 3, all.size());
    }

    // ---------- findByApplicationId ----------

    /** findByApplicationId() finds a saved application (case-insensitive). */
    public void testFindByApplicationIdFindsExisting() {
        dao.save(createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted"));
        Application found = dao.findByApplicationId("app-001");
        assertNotNull("should find with case-insensitive ID", found);
        assertEquals("APP-001", found.getApplicationId());
    }

    /** findByApplicationId() returns null for a non-existent ID. */
    public void testFindByApplicationIdReturnsNullForNonExistent() {
        Application found = dao.findByApplicationId("NONEXIST");
        assertNull("non-existent ID should return null", found);
    }

    /** findByApplicationId() returns null for a null argument. */
    public void testFindByApplicationIdReturnsNullForNull() {
        Application found = dao.findByApplicationId(null);
        assertNull("null argument should return null", found);
    }

    /** findByApplicationId() returns null for a blank string. */
    public void testFindByApplicationIdReturnsNullForBlank() {
        Application found = dao.findByApplicationId("   ");
        assertNull("blank string should return null", found);
    }

    // ---------- findByJobId ----------

    /** findByJobId() returns matching applications. */
    public void testFindByJobIdReturnsMatchingApplications() {
        dao.save(createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted"));
        dao.save(createApplication("APP-002", "JOB-002", "STU-002", "Bob Smith", "submitted"));
        dao.save(createApplication("APP-003", "JOB-001", "STU-003", "Charlie Brown", "approved"));

        List<Application> result = dao.findByJobId("JOB-001");
        assertEquals("should have two matching records", 2, result.size());

        List<Application> noMatch = dao.findByJobId("JOB-999");
        assertTrue("non-matching Job should return empty list", noMatch.isEmpty());
    }

    // ---------- findByTaStudentId ----------

    /** findByTaStudentId() returns matching applications. */
    public void testFindByTaStudentIdReturnsMatchingApplications() {
        dao.save(createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted"));
        dao.save(createApplication("APP-002", "JOB-002", "STU-001", "Alice Johnson", "approved"));
        dao.save(createApplication("APP-003", "JOB-003", "STU-003", "Charlie Brown", "submitted"));

        List<Application> result = dao.findByTaStudentId("STU-001");
        assertEquals("should have two matching records", 2, result.size());

        List<Application> noMatch = dao.findByTaStudentId("STU-999");
        assertTrue("non-matching student ID should return empty list", noMatch.isEmpty());
    }

    // ---------- hasApplied ----------

    /** hasApplied() returns true when the application exists. */
    public void testHasAppliedReturnsTrueAfterApplication() {
        dao.save(createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted"));
        assertTrue("already applied should return true (case-insensitive)",
                dao.hasApplied("job-001", "stu-001"));
    }

    /** hasApplied() returns false for a different job. */
    public void testHasAppliedReturnsFalseForDifferentJob() {
        dao.save(createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted"));
        assertFalse("different job should return false",
                dao.hasApplied("JOB-002", "STU-001"));
    }

    // ---------- update ----------

    /** update() updates an existing application. */
    public void testUpdateUpdatesExistingApplication() {
        Application app = createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "submitted");
        dao.save(app);

        Application updated = createApplication("APP-001", "JOB-001", "STU-001", "Alice Johnson", "approved");
        updated.setDecisionNote("Approved");
        boolean result = dao.update(updated);
        assertTrue("update() should return true", result);

        Application found = dao.findByApplicationId("APP-001");
        assertNotNull("should still be found after update", found);
        assertEquals("status should be updated to approved", "approved", found.getStatus());
        assertEquals("decisionNote should be updated", "Approved", found.getDecisionNote());
    }

    /** update() returns false for a non-existent application. */
    public void testUpdateReturnsFalseForNonExistentApplication() {
        Application app = createApplication("NONEXIST", "JOB-001", "STU-001", "Alice Johnson", "submitted");
        boolean result = dao.update(app);
        assertFalse("non-existent application should return false", result);
    }

    // ---------- Helper methods ----------

    private Application createApplication(String appId, String jobId, String taStudentId, String taName, String status) {
        Application app = new Application();
        app.setApplicationId(appId);
        app.setJobId(jobId);
        app.setTaStudentId(taStudentId);
        app.setTaName(taName);
        app.setStatus(status);
        app.setSubmittedAt("2025-01-01 10:00:00");
        app.setUpdatedAt("2025-01-01 10:00:00");
        return app;
    }

    public static void main(String[] args) {
        new ApplicationDaoTest().runTestsAndExit();
    }
}
