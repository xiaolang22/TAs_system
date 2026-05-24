package com.group19.dao;

import com.group19.TestRunner;
import com.group19.model.TA;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for TADao. Uses temporary JSON files for data read/write verification.
 *
 * @author Group19
 * @since 1.0
 */
public class TADaoTest extends TestRunner {

    private Path tempFile;
    private TADao dao;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("test-tas-", ".json");
        Files.writeString(tempFile, "[]");
        dao = new TADao(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    // ---------- findAll ----------

    /** findAll() reads data from the temporary file; returns empty initially. */
    public void testFindAllReadsFromFile() throws Exception {
        // Write one record to the temporary file first
        String json = "[{\"name\":\"测试TA\",\"studentId\":\"S001\",\"email\":\"test@test.com\","
                + "\"programme\":\"计算机科学\",\"skills\":\"Java\",\"experience\":\"2年\","
                + "\"availability\":\"全职\",\"cvFilePath\":\"\",\"updatedAt\":\"\"}]";
        Files.writeString(tempFile, json);

        List<TA> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertEquals("should have one record", 1, result.size());
        assertEquals("测试TA", result.get(0).getName());
        assertEquals("S001", result.get(0).getStudentId());
    }

    /** findAll() returns an empty list when the file is initially empty. */
    public void testFindAllReturnsEmptyForEmptyFile() throws Exception {
        List<TA> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertTrue("empty file should return empty list", result.isEmpty());
    }

    // ---------- findByStudentId ----------

    /** findByStudentId() finds a matching TA. */
    public void testFindByStudentIdFindsMatchingTA() throws Exception {
        // Write test data
        Files.writeString(tempFile,
                "[{\"name\":\"Alice Johnson\",\"studentId\":\"STU-001\",\"email\":\"zhang@test.com\"},"
                + "{\"name\":\"Bob Smith\",\"studentId\":\"STU-002\",\"email\":\"li@test.com\"}]");

        TA found = dao.findByStudentId("STU-001");
        assertNotNull("should find STU-001", found);
        assertEquals("Alice Johnson", found.getName());

        TA foundCase = dao.findByStudentId("stu-001");
        assertNotNull("case-insensitive lookup should match", foundCase);
    }

    /** findByStudentId() returns null for a non-existent student ID. */
    public void testFindByStudentIdReturnsNullForNonExistent() throws Exception {
        Files.writeString(tempFile,
                "[{\"name\":\"Alice Johnson\",\"studentId\":\"STU-001\"}]");

        TA found = dao.findByStudentId("NONEXIST");
        assertNull("non-existent student ID should return null", found);
    }

    // ---------- saveOrUpdate ----------

    /** saveOrUpdate() inserts a new TA. */
    public void testSaveOrUpdateInsertsNewTA() throws Exception {
        TA ta = createTA("新TA", "STU-NEW", "new@test.com", "软件工程", "Python", "兼职");
        TA result = dao.saveOrUpdate(ta);
        assertNotNull("should return the saved TA", result);
        assertEquals("STU-NEW", result.getStudentId());

        List<TA> all = dao.findAll();
        assertEquals("should have one record", 1, all.size());
    }

    /** saveOrUpdate() updates an existing TA (same student ID). */
    public void testSaveOrUpdateUpdatesExistingTA() throws Exception {
        // Insert one first
        TA original = createTA("原始TA", "STU-001", "old@test.com", "计算机", "Java", "全职");
        dao.saveOrUpdate(original);

        // Save new data with the same student ID
        TA updated = createTA("更新TA", "STU-001", "new@test.com", "软件工程", "Python,C++", "兼职");
        dao.saveOrUpdate(updated);

        List<TA> all = dao.findAll();
        assertEquals("should still have only one record", 1, all.size());
        assertEquals("name should be updated", "更新TA", all.get(0).getName());
        assertEquals("email should be updated", "new@test.com", all.get(0).getEmail());
    }

    // ---------- Helper methods ----------

    private TA createTA(String name, String studentId, String email, String programme,
                        String skills, String availability) {
        TA ta = new TA(name, studentId, email, programme, skills, availability);
        ta.setExperience("有经验");
        ta.setCvFilePath("/path/to/cv.pdf");
        ta.setUpdatedAt("2025-01-01 10:00:00");
        return ta;
    }

    public static void main(String[] args) {
        new TADaoTest().runTestsAndExit();
    }
}
