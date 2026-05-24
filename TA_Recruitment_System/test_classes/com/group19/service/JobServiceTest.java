package com.group19.service;

import com.group19.TestRunner;
import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 单元测试：JobService 职位服务
 * 使用临时 JSON 文件作为 JobDao 的数据源。
 *
 * @author Group19
 */
public class JobServiceTest extends TestRunner {

    private Path tempFile;
    private JobDao jobDao;
    private JobService jobService;

    /**
     * 创建临时 JSON 文件，写入空数组。
     */
    private static Path createTempJsonFile() throws IOException {
        Path tempFile = Files.createTempFile("test-jobs-", ".json");
        Files.writeString(tempFile, "[]");
        return tempFile;
    }

    /**
     * 每个测试方法执行前：创建临时文件并初始化 DAO 和服务。
     */
    @Override
    protected void setUp() throws Exception {
        tempFile = createTempJsonFile();
        jobDao = new JobDao(tempFile);
        jobService = new JobService(jobDao);
    }

    /**
     * 每个测试方法执行后：删除临时文件。
     */
    @Override
    protected void tearDown() throws Exception {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    // ---- Helper methods ----

    /** 创建一个带完整字段的 Job 草稿。 */
    private Job createDraftJob(String title, String description, String requirements,
                                String hours, String schedule, String deadline, String ownerMoUserId) {
        Job job = new Job();
        job.setTitle(title);
        job.setCategory("Grad");
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setHours(hours);
        job.setSchedule(schedule);
        job.setDeadline(deadline);
        job.setOwnerMoUserId(ownerMoUserId);
        return job;
    }

    // ==================== createJob 测试 ====================

    /** 有效数据创建职位应成功。 */
    public void testCreateJobWithValidData() {
        Job job = createDraftJob("Valid Job", "Description", "Java", "10", "Mon-Wed", "2026-06-15", "MO001");
        ServiceResult<Job> result = jobService.createJob(job);
        assertTrue("Job creation should succeed: " + result.getMessage(), result.isSuccess());
        assertNotNull("Result data should not be null", result.getData());
    }

    /** 标题为空时创建失败。 */
    public void testCreateJobWithEmptyTitle() {
        Job job = createDraftJob("", "Description", "Java", "10", "Mon-Wed", "2026-06-15", "MO001");
        ServiceResult<Job> result = jobService.createJob(job);
        assertFalse("Empty title should fail", result.isSuccess());
        assertTrue("Error should mention title", result.getMessage().toLowerCase().contains("title"));
    }

    /** 描述为空时创建失败。 */
    public void testCreateJobWithEmptyDescription() {
        Job job = createDraftJob("Title", "", "Java", "10", "Mon-Wed", "2026-06-15", "MO001");
        ServiceResult<Job> result = jobService.createJob(job);
        assertFalse("Empty description should fail", result.isSuccess());
    }

    /** 创建职位后自动设置 OPEN 状态并生成唯一 ID。 */
    public void testCreateJobSetsOpenStatusAndGeneratedId() {
        Job job = createDraftJob("Status Test Job", "Description", "Java", "10", "Mon-Wed", "2026-06-15", "MO001");
        ServiceResult<Job> result = jobService.createJob(job);
        assertTrue("Creation should succeed", result.isSuccess());

        Job created = result.getData();
        assertNotNull("Job ID should be generated", created.getJobId());
        assertFalse("Job ID should not be empty", created.getJobId().isEmpty());
        assertEquals("Status should be OPEN", "OPEN", created.getStatus());
        assertNotNull("CreatedAt should be set", created.getCreatedAt());
    }

    // ==================== findById 测试 ====================

    /** 根据有效 ID 查找应返回对应职位。 */
    public void testFindByIdReturnsJobForValidId() {
        Job job = createDraftJob("Find Job", "Description", "Java", "10", "Mon-Wed", "2026-06-15", "MO001");
        ServiceResult<Job> created = jobService.createJob(job);
        Job found = jobService.findById(created.getData().getJobId());
        assertNotNull("Should find job by valid ID", found);
        assertEquals("Title should match", "Find Job", found.getTitle());
    }

    /** 根据无效 ID 查找应返回 null。 */
    public void testFindByIdReturnsNullForInvalidId() {
        assertNull("Invalid ID should return null", jobService.findById("NONEXISTENT"));
    }

    // ==================== findAllJobs 测试 ====================

    /** findAllJobs 应返回所有已创建的职位。 */
    public void testFindAllJobsReturnsAllJobs() {
        jobService.createJob(createDraftJob("Job 1", "Desc 1", "Java", "10", "Mon", "2026-07-01", "MO001"));
        jobService.createJob(createDraftJob("Job 2", "Desc 2", "Python", "5", "Tue", "2026-07-15", "MO001"));
        List<Job> jobs = jobService.findAllJobs();
        assertEquals("Should return 2 jobs", 2, jobs.size());
    }

    // ==================== isOwnedBy 测试 ====================

    /** 职位拥有者匹配时返回 true。 */
    public void testIsOwnedByReturnsTrueForMatchingOwner() {
        Job job = createDraftJob("Owner Job", "Description", "Java", "10", "Mon", "2026-06-15", "MO001");
        jobService.createJob(job);
        assertTrue("MO001 should own this job", jobService.isOwnedBy(job, "MO001"));
    }

    /** 职位拥有者不匹配时返回 false。 */
    public void testIsOwnedByReturnsFalseForDifferentOwner() {
        Job job = createDraftJob("Owner Job 2", "Description", "Java", "10", "Mon", "2026-06-15", "MO001");
        jobService.createJob(job);
        assertFalse("MO002 should not own this job", jobService.isOwnedBy(job, "MO002"));
    }

    // ==================== parseDeadlineDate 测试 ====================

    /** 解析有效的 ISO 日期字符串。 */
    public void testParseDeadlineDateParsesValidIsoDate() {
        LocalDate date = JobService.parseDeadlineDate("2026-06-15");
        assertNotNull("Valid date should be parsed", date);
        assertEquals("Year should match", 2026, date.getYear());
        assertEquals("Month should match", 6, date.getMonthValue());
        assertEquals("Day should match", 15, date.getDayOfMonth());
    }

    /** 传入 null 应返回 null。 */
    public void testParseDeadlineDateReturnsNullForNull() {
        assertNull("Null input should return null", JobService.parseDeadlineDate(null));
    }

    /** 解析有效的 ISO 日期时间字符串。 */
    public void testParseDeadlineDateParsesIsoDateTime() {
        LocalDate date = JobService.parseDeadlineDate("2026-06-15T23:59:59");
        assertNotNull("Valid datetime should be parsed", date);
        assertEquals("Year should be 2026", 2026, date.getYear());
    }

    // ==================== filterJobs 测试 ====================

    /** 按关键字过滤返回匹配的职位。 */
    public void testFilterJobsFiltersByKeyword() {
        jobService.createJob(createDraftJob("Python Tutor", "Teach Python", "Python", "10", "Mon", "2026-07-01", "MO001"));
        jobService.createJob(createDraftJob("Java Tutor", "Teach Java", "Java", "10", "Tue", "2026-07-01", "MO001"));
        List<Job> all = jobService.findAllJobs();
        List<Job> filtered = jobService.filterJobs(all, "Python", null, null);
        assertEquals("Should filter to 1 job", 1, filtered.size());
        assertEquals("Title should be Python Tutor", "Python Tutor", filtered.get(0).getTitle());
    }

    /** 关键字为 null 时返回全部职位。 */
    public void testFilterJobsReturnsAllWhenKeywordIsNull() {
        jobService.createJob(createDraftJob("Job A", "Desc A", "Skills", "10", "Mon", "2026-07-01", "MO001"));
        List<Job> all = jobService.findAllJobs();
        List<Job> filtered = jobService.filterJobs(all, null, null, null);
        assertEquals("Should return all jobs when keyword is null", all.size(), filtered.size());
    }

    // ==================== 额外边界测试 ====================

    /** 空列表传入 filterJobs 应返回空列表。 */
    public void testFilterJobsEmptyList() {
        List<Job> filtered = jobService.filterJobs(new ArrayList<>(), "test", null, null);
        assertTrue("Empty input should yield empty output", filtered.isEmpty());
    }

    /** null 列表传入 filterJobs 应返回空列表。 */
    public void testFilterJobsNullList() {
        List<Job> filtered = jobService.filterJobs(null, "test", null, null);
        assertTrue("Null input should yield empty output", filtered.isEmpty());
    }

    /** findOpenActiveJobs 应过滤掉 CLOSED 状态的职位。 */
    public void testFindOpenActiveJobsFiltersCorrectly() {
        Job openJob = createDraftJob("Open Job", "Desc", "Skills", "10", "Mon", "2099-12-31", "MO001");
        openJob.setStatus("OPEN");
        Job closedJob = createDraftJob("Closed Job", "Desc", "Skills", "10", "Mon", "2099-12-31", "MO001");
        closedJob.setStatus("CLOSED");
        jobDao.save(openJob);
        jobDao.save(closedJob);

        List<Job> openJobs = jobService.findOpenActiveJobs(LocalDate.of(2026, 5, 24));
        assertEquals("Should find 1 open job", 1, openJobs.size());
        assertEquals("Should be Open Job", "Open Job", openJobs.get(0).getTitle());
    }

    // ---- main ----

    public static void main(String[] args) {
        new JobServiceTest().runTests();
    }
}
