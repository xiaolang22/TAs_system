package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.SavedJob;

/**
 * Unit tests for the SavedJob model class.
 * Tests default constructor, parameterised constructor, and all 3 getter/setter pairs.
 *
 * @author Group19
 * @since 1.0
 */
public class SavedJobTest extends TestRunner {

    public void testDefaultConstructorInitialisesAllFieldsToNull() {
        SavedJob savedJob = new SavedJob();
        assertNotNull("Default constructor should create a non-null SavedJob object", savedJob);
        assertNull("userId should be null after default constructor", savedJob.getUserId());
        assertNull("jobId should be null after default constructor", savedJob.getJobId());
        assertNull("savedAt should be null after default constructor", savedJob.getSavedAt());
    }

    public void testParameterisedConstructorSetsAllFields() {
        SavedJob savedJob = new SavedJob("STU001", "JOB001", "2024-01-15T10:00:00");
        assertNotNull("Parameterised constructor should create a non-null SavedJob object", savedJob);
        assertEquals("STU001", savedJob.getUserId());
        assertEquals("JOB001", savedJob.getJobId());
        assertEquals("2024-01-15T10:00:00", savedJob.getSavedAt());
    }

    public void testSetAndGetUserId() {
        SavedJob savedJob = new SavedJob();
        savedJob.setUserId("STU002");
        assertEquals("STU002", savedJob.getUserId());
    }

    public void testSetAndGetJobId() {
        SavedJob savedJob = new SavedJob();
        savedJob.setJobId("JOB002");
        assertEquals("JOB002", savedJob.getJobId());
    }

    public void testSetAndGetSavedAt() {
        SavedJob savedJob = new SavedJob();
        savedJob.setSavedAt("2024-02-20T09:00:00");
        assertEquals("2024-02-20T09:00:00", savedJob.getSavedAt());
    }

    public void testDefaultConstructorThenSettersEqualToParameterisedConstructor() {
        SavedJob sj1 = new SavedJob("STU003", "JOB003", "2024-03-01T08:00:00");
        SavedJob sj2 = new SavedJob();
        sj2.setUserId("STU003");
        sj2.setJobId("JOB003");
        sj2.setSavedAt("2024-03-01T08:00:00");

        assertEquals(sj1.getUserId(), sj2.getUserId());
        assertEquals(sj1.getJobId(), sj2.getJobId());
        assertEquals(sj1.getSavedAt(), sj2.getSavedAt());
    }

    public static void main(String[] args) {
        new SavedJobTest().runTestsAndExit();
    }
}
