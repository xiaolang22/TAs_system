package com.group19.service;

import com.group19.TestRunner;
import com.group19.dao.JobDao;
import com.group19.dao.SavedJobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * unit test：SavedJobService 收藏职位服务
 * 使用临时 JSON 文件作为 SavedJobDao 和 JobDao 的数据源。
 *
 * @author Group19
 */
public class SavedJobServiceTest extends TestRunner {

    private Path savedJobTempFile;
    private Path jobTempFile;
    private SavedJobDao savedJobDao;
    private JobDao jobDao;
    private SavedJobService savedJobService;

    /**
     * 创建临时 JSON 文件，写入空数组。
     */
    private static Path createTempJsonFile() throws IOException {
        Path tempFile = Files.createTempFile("test-savedjobs-", ".json");
        Files.writeString(tempFile, "[]");
        return tempFile;
    }

    /**
     * 每个测试方法执行前：创建临时文件、初始化 DAO 和服务，并预置测试职位。
     */
    @Override
    protected void setUp() throws Exception {
        savedJobTempFile = createTempJsonFile();
        jobTempFile = createTempJsonFile();
        savedJobDao = new SavedJobDao(savedJobTempFile);
        jobDao = new JobDao(jobTempFile);
        savedJobService = new SavedJobService(savedJobDao, jobDao);

        // 预置一个测试职位，saveJob 要求职位存在
        Job job = new Job();
        job.setJobId("JOB-001");
        job.setTitle("Test Position");
        job.setCategory("Grad");
        job.setDescription("A test job position.");
        job.setRequirements("Java");
        job.setHours("10");
        job.setSchedule("Mon");
        job.setDeadline("2026-12-31");
        job.setStatus("OPEN");
        job.setCreatedAt("2026-05-24T10:00:00");
        job.setOwnerMoUserId("MO001");
        jobDao.save(job);
    }

    /**
     * 每个测试方法执行后：删除临时文件。
     */
    @Override
    protected void tearDown() throws Exception {
        if (savedJobTempFile != null) {
            Files.deleteIfExists(savedJobTempFile);
        }
        if (jobTempFile != null) {
            Files.deleteIfExists(jobTempFile);
        }
    }

    // ==================== saveJob 测试 ====================

    /** 有效 userId 和 jobId 保存收藏应成功。 */
    public void testSaveJobSucceedsWithValidUserIdAndJobId() {
        ServiceResult<Void> result = savedJobService.saveJob("STU001", "JOB-001");
        assertTrue("Save should succeed: " + result.getMessage(), result.isSuccess());
    }

    /** userId 为空时保存失败。 */
    public void testSaveJobFailsWithEmptyUserId() {
        ServiceResult<Void> result = savedJobService.saveJob("", "JOB-001");
        assertFalse("Empty userId should fail", result.isSuccess());
    }

    // ==================== isSaved 测试 ====================

    /** 保存后 isSaved 返回 true。 */
    public void testIsSavedReturnsTrueAfterSaving() {
        savedJobService.saveJob("STU001", "JOB-001");
        assertTrue("isSaved should return true after saving",
                savedJobService.isSaved("STU001", "JOB-001"));
    }

    /** 未保存时 isSaved 返回 false。 */
    public void testIsSavedReturnsFalseBeforeSaving() {
        assertFalse("isSaved should return false before saving",
                savedJobService.isSaved("STU001", "JOB-001"));
    }

    // ==================== removeSavedJob 测试 ====================

    /** 删除收藏后 isSaved 返回 false。 */
    public void testRemoveSavedJobRemovesSavedJob() {
        savedJobService.saveJob("STU001", "JOB-001");
        ServiceResult<Void> result = savedJobService.removeSavedJob("STU001", "JOB-001");
        assertTrue("Remove should succeed", result.isSuccess());
        assertFalse("isSaved should return false after removal",
                savedJobService.isSaved("STU001", "JOB-001"));
    }

    // ==================== findSavedJobs 测试 ====================

    /** 根据 userId 查找应返回已收藏的职位列表。 */
    public void testFindSavedJobsReturnsSavedJobsForUser() {
        savedJobService.saveJob("STU001", "JOB-001");
        List<Job> saved = savedJobService.findSavedJobs("STU001");
        assertEquals("Should find 1 saved job", 1, saved.size());
        assertEquals("Job title should match", "Test Position", saved.get(0).getTitle());
    }

    /** 无收藏记录时应返回空列表。 */
    public void testFindSavedJobsReturnsEmptyForNoSavedJobs() {
        List<Job> saved = savedJobService.findSavedJobs("STU999");
        assertTrue("Should be empty for user with no saved jobs", saved.isEmpty());
    }

    /** findSavedJobIds 应返回收藏的职位 ID 集合。 */
    public void testFindSavedJobIdsReturnsSavedJobIds() {
        savedJobService.saveJob("STU001", "JOB-001");
        Set<String> ids = savedJobService.findSavedJobIds("STU001");
        assertTrue("Saved IDs should contain JOB-001", ids.contains("JOB-001"));
    }

    // ---- main ----

    public static void main(String[] args) {
        new SavedJobServiceTest().runTests();
    }
}
