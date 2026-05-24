package com.group19.dao;

import com.group19.TestRunner;
import com.group19.model.SavedJob;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for SavedJobDao. Uses temporary JSON files for data read/write verification.
 *
 * @author Group19
 * @since 1.0
 */
public class SavedJobDaoTest extends TestRunner {

    private Path tempFile;
    private SavedJobDao dao;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("test-savedjobs-", ".json");
        Files.writeString(tempFile, "[]");
        dao = new SavedJobDao(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    // ---------- findAll ----------

    /** findAll() returns an empty list initially. */
    public void testFindAllReturnsEmptyInitially() {
        List<SavedJob> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertTrue("initial list should be empty", result.isEmpty());
    }

    // ---------- save ----------

    /** save() adds a new saved job record. */
    public void testSaveAddsSavedJobRecord() {
        SavedJob sj = new SavedJob("USER-001", "JOB-001", "2025-01-01 10:00:00");
        boolean saved = dao.save(sj);
        assertTrue("save() should return true", saved);

        List<SavedJob> all = dao.findAll();
        assertEquals("one record should exist after saving", 1, all.size());
        assertEquals("USER-001", all.get(0).getUserId());
        assertEquals("JOB-001", all.get(0).getJobId());
    }

    /** save() returns true for duplicate user+job without error (idempotent). */
    public void testSaveDuplicateUserAndJobReturnsTrueWithoutError() {
        SavedJob sj = new SavedJob("USER-001", "JOB-001", "2025-01-01 10:00:00");
        assertTrue("first save should return true", dao.save(sj));

        // Duplicate save of same user+job
        SavedJob dup = new SavedJob("USER-001", "JOB-001", "2025-02-01 10:00:00");
        boolean result = dao.save(dup);
        assertTrue("duplicate save should return true without error", result);

        // List should still have only one record
        List<SavedJob> all = dao.findAll();
        assertEquals("duplicate save should not increase record count", 1, all.size());
    }

    // ---------- findByUserId ----------

    /** findByUserId() returns matching records. */
    public void testFindByUserIdReturnsMatchingRecords() {
        dao.save(new SavedJob("USER-001", "JOB-001", "2025-01-01 10:00:00"));
        dao.save(new SavedJob("USER-001", "JOB-002", "2025-01-02 10:00:00"));
        dao.save(new SavedJob("USER-002", "JOB-001", "2025-01-03 10:00:00"));

        List<SavedJob> result = dao.findByUserId("USER-001");
        assertEquals("USER-001 should have two records", 2, result.size());

        List<SavedJob> noMatch = dao.findByUserId("USER-999");
        assertTrue("non-existent user should return empty list", noMatch.isEmpty());
    }

    // ---------- findByUserIdAndJobId ----------

    /** findByUserIdAndJobId() returns the matching record. */
    public void testFindByUserIdAndJobIdReturnsMatchingRecord() {
        dao.save(new SavedJob("USER-001", "JOB-001", "2025-01-01 10:00:00"));
        dao.save(new SavedJob("USER-001", "JOB-002", "2025-01-02 10:00:00"));

        SavedJob found = dao.findByUserIdAndJobId("USER-001", "JOB-001");
        assertNotNull("should find matching record", found);
        assertEquals("JOB-001", found.getJobId());

        SavedJob noMatch = dao.findByUserIdAndJobId("USER-001", "JOB-999");
        assertNull("non-existent combination should return null", noMatch);
    }

    // ---------- delete ----------

    /** delete() removes a record. */
    public void testDeleteRemovesRecord() {
        dao.save(new SavedJob("USER-001", "JOB-001", "2025-01-01 10:00:00"));
        dao.save(new SavedJob("USER-001", "JOB-002", "2025-01-02 10:00:00"));

        assertEquals("two records should exist before delete", 2, dao.findAll().size());

        boolean deleted = dao.delete("USER-001", "JOB-001");
        assertTrue("delete() should return true", deleted);

        List<SavedJob> remaining = dao.findAll();
        assertEquals("one record should remain after delete", 1, remaining.size());
        assertEquals("JOB-002", remaining.get(0).getJobId());
    }

    public static void main(String[] args) {
        new SavedJobDaoTest().runTestsAndExit();
    }
}
