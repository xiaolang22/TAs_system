package com.group19.dao;

import com.group19.TestRunner;
import com.group19.model.Job;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for JobDao. Uses temporary JSON files for data read/write verification.
 *
 * @author Group19
 * @since 1.0
 */
public class JobDaoTest extends TestRunner {

    private Path tempFile;
    private JobDao dao;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("test-jobs-", ".json");
        Files.writeString(tempFile, "[]");
        dao = new JobDao(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    // ---------- findAll ----------

    /** findAll() returns an empty list initially. */
    public void testFindAllReturnsEmptyListInitially() {
        List<Job> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertTrue("initial list should be empty", result.isEmpty());
    }

    // ---------- save ----------

    /** save() adds a new job that can be found in the list afterwards. */
    public void testSaveAddsJob() {
        Job job = createJob("JOB-001", "Java 助教", "open");
        boolean saved = dao.save(job);
        assertTrue("save() should return true", saved);

        List<Job> all = dao.findAll();
        assertEquals("one record should exist after saving", 1, all.size());
        assertEquals("JOB-001", all.get(0).getJobId());
    }

    // ---------- findById ----------

    /** findById() finds a saved job. */
    public void testFindByIdFindsSavedJob() {
        dao.save(createJob("JOB-001", "Java 助教", "open"));
        Job found = dao.findById("JOB-001");
        assertNotNull("should find the saved job", found);
        assertEquals("Java 助教", found.getTitle());
    }

    /** findById() returns null for a non-existent ID. */
    public void testFindByIdReturnsNullForNonExistent() {
        Job found = dao.findById("NONEXIST");
        assertNull("non-existent ID should return null", found);
    }

    // ---------- update ----------

    /** update() updates an existing job. */
    public void testUpdateUpdatesExistingJob() {
        dao.save(createJob("JOB-001", "Java 助教", "open"));

        Job updated = createJob("JOB-001", "Java 高级助教", "closed");
        updated.setDescription("Updated description");
        boolean result = dao.update(updated);
        assertTrue("update() should return true", result);

        Job found = dao.findById("JOB-001");
        assertNotNull("should still be found after update", found);
        assertEquals("title should be updated", "Java 高级助教", found.getTitle());
        assertEquals("status should be updated to closed", "closed", found.getStatus());
    }

    /** update() returns false for a null or blank jobId. */
    public void testUpdateReturnsFalseForNullOrBlankJobId() {
        Job jobWithNullId = new Job();
        jobWithNullId.setTitle("测试");
        assertFalse("null jobId should return false", dao.update(jobWithNullId));

        Job jobWithBlankId = new Job();
        jobWithBlankId.setJobId("   ");
        jobWithBlankId.setTitle("测试");
        assertFalse("blank jobId should return false", dao.update(jobWithBlankId));
    }

    // ---------- delete ----------

    /** delete() removes an existing job. */
    public void testDeleteRemovesExistingJob() {
        dao.save(createJob("JOB-001", "Java 助教", "open"));
        assertEquals("one record should exist before delete", 1, dao.findAll().size());

        boolean deleted = dao.delete("JOB-001");
        assertTrue("delete() should return true", deleted);
        assertTrue("list should be empty after delete", dao.findAll().isEmpty());
    }

    /** delete() returns false for a non-existent job. */
    public void testDeleteReturnsFalseForNonExistentJob() {
        boolean deleted = dao.delete("NONEXIST");
        assertFalse("non-existent job should return false", deleted);
    }

    // ---------- Helper methods ----------

    private Job createJob(String jobId, String title, String status) {
        Job job = new Job();
        job.setJobId(jobId);
        job.setTitle(title);
        job.setStatus(status);
        job.setCategory("Grad");
        job.setDescription("Job description");
        job.setRequirements("Job requirements");
        job.setHours("10");
        job.setSchedule("Flexible");
        job.setDeadline("2025-06-01");
        job.setCreatedAt("2025-01-01");
        job.setOwnerMoUserId("MO001");
        return job;
    }

    public static void main(String[] args) {
        new JobDaoTest().runTestsAndExit();
    }
}
