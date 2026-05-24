package com.group19.service;

import com.group19.TestRunner;
import com.group19.dao.ApplicationDao;
import com.group19.dao.TimelineDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * unit test：ApplicationService 申请服务
 * 使用临时 JSON 文件作为 ApplicationDao 和 TimelineDao 的数据源。
 *
 * @author Group19
 */
public class ApplicationServiceTest extends TestRunner {

    private Path appTempFile;
    private Path timelineTempFile;
    private ApplicationDao applicationDao;
    private ApplicationTimelineRecorder timelineRecorder;
    private ApplicationService applicationService;

    /**
     * 创建临时 JSON 文件，写入空数组。
     */
    private static Path createTempJsonFile() throws IOException {
        Path tempFile = Files.createTempFile("test-apps-", ".json");
        Files.writeString(tempFile, "[]");
        return tempFile;
    }

    /**
     * 每个测试方法执行前：创建临时文件并初始化所有依赖。
     */
    @Override
    protected void setUp() throws Exception {
        appTempFile = createTempJsonFile();
        timelineTempFile = createTempJsonFile();
        applicationDao = new ApplicationDao(appTempFile);
        timelineRecorder = new ApplicationTimelineRecorder(new TimelineDao(timelineTempFile));
        applicationService = new ApplicationService(applicationDao, timelineRecorder);
    }

    /**
     * 每个测试方法执行后：删除临时文件。
     */
    @Override
    protected void tearDown() throws Exception {
        if (appTempFile != null) {
            Files.deleteIfExists(appTempFile);
        }
        if (timelineTempFile != null) {
            Files.deleteIfExists(timelineTempFile);
        }
    }

    // ==================== applyForJob 测试 ====================

    /** 有效数据提交申请应成功。 */
    public void testApplyForJobWithValidData() {
        ServiceResult<Application> result = applicationService.applyForJob(
                "JOB-001", "STU001", "Test User", "/uploads/cv.pdf");
        assertTrue("Application should succeed: " + result.getMessage(), result.isSuccess());
        assertNotNull("Application data should not be null", result.getData());
        assertEquals("Status should be SUBMITTED", "SUBMITTED", result.getData().getStatus());
    }

    /** jobId 为空时提交失败。 */
    public void testApplyForJobWithEmptyJobId() {
        ServiceResult<Application> result = applicationService.applyForJob(
                "", "STU001", "Test User", "/uploads/cv.pdf");
        assertFalse("Empty jobId should fail", result.isSuccess());
    }

    /** taStudentId 为空时提交失败。 */
    public void testApplyForJobWithEmptyTaStudentId() {
        ServiceResult<Application> result = applicationService.applyForJob(
                "JOB-001", "", "Test User", "/uploads/cv.pdf");
        assertFalse("Empty taStudentId should fail", result.isSuccess());
    }

    /** taName 为空时提交失败。 */
    public void testApplyForJobWithEmptyTaName() {
        ServiceResult<Application> result = applicationService.applyForJob(
                "JOB-001", "STU001", "", "/uploads/cv.pdf");
        assertFalse("Empty taName should fail", result.isSuccess());
    }

    /** cvFilePath 为空时提交失败。 */
    public void testApplyForJobWithEmptyCvFilePath() {
        ServiceResult<Application> result = applicationService.applyForJob(
                "JOB-001", "STU001", "Test User", "");
        assertFalse("Empty cvFilePath should fail", result.isSuccess());
    }

    /** 重复提交申请应被阻止。 */
    public void testApplyForJobPreventsDuplicateApplication() {
        applicationService.applyForJob("JOB-001", "STU001", "Test User", "/uploads/cv.pdf");
        ServiceResult<Application> result = applicationService.applyForJob(
                "JOB-001", "STU001", "Test User", "/uploads/cv.pdf");
        assertFalse("Duplicate application should fail", result.isSuccess());
        assertTrue("Error message should mention 'already applied'",
                result.getMessage().contains("already applied"));
    }

    // ==================== getApplicationsByJobId 测试 ====================

    /** 根据 jobId 查询应返回匹配的申请列表。 */
    public void testGetApplicationsByJobIdReturnsMatchingApplications() {
        applicationService.applyForJob("JOB-A", "STU1", "User1", "/uploads/cv1.pdf");
        applicationService.applyForJob("JOB-A", "STU2", "User2", "/uploads/cv2.pdf");
        applicationService.applyForJob("JOB-B", "STU3", "User3", "/uploads/cv3.pdf");

        List<Application> results = applicationService.getApplicationsByJobId("JOB-A");
        assertEquals("Should find 2 applications for JOB-A", 2, results.size());
    }

    // ==================== isValidStatus 测试 ====================

    /** 合法状态should return true。 */
    public void testIsValidStatusAcceptsValidStatuses() {
        assertTrue("SUBMITTED", ApplicationService.isValidStatus("SUBMITTED"));
        assertTrue("IN_REVIEW", ApplicationService.isValidStatus("IN_REVIEW"));
        assertTrue("SHORTLISTED", ApplicationService.isValidStatus("SHORTLISTED"));
        assertTrue("ACCEPTED", ApplicationService.isValidStatus("ACCEPTED"));
        assertTrue("REJECTED", ApplicationService.isValidStatus("REJECTED"));
    }

    /** 非法状态should return false。 */
    public void testIsValidStatusRejectsInvalidStatuses() {
        assertFalse("INVALID", ApplicationService.isValidStatus("INVALID"));
        assertFalse("PENDING", ApplicationService.isValidStatus("PENDING"));
        assertFalse("APPROVED", ApplicationService.isValidStatus("APPROVED"));
    }

    // ==================== canTransition 测试 ====================

    /** 允许向前状态转换。 */
    public void testCanTransitionAllowsForwardTransitions() {
        assertTrue("SUBMITTED -> IN_REVIEW",
                ApplicationService.canTransition("SUBMITTED", "IN_REVIEW"));
        assertTrue("IN_REVIEW -> SHORTLISTED",
                ApplicationService.canTransition("IN_REVIEW", "SHORTLISTED"));
        assertTrue("SHORTLISTED -> ACCEPTED",
                ApplicationService.canTransition("SHORTLISTED", "ACCEPTED"));
        assertTrue("SHORTLISTED -> REJECTED",
                ApplicationService.canTransition("SHORTLISTED", "REJECTED"));
        // 同状态转换也允许
        assertTrue("SUBMITTED -> SUBMITTED (same status allowed)",
                ApplicationService.canTransition("SUBMITTED", "SUBMITTED"));
    }

    /** 拒绝向后状态转换。 */
    public void testCanTransitionRejectsBackwardTransitions() {
        assertFalse("IN_REVIEW -> SUBMITTED",
                ApplicationService.canTransition("IN_REVIEW", "SUBMITTED"));
        assertFalse("ACCEPTED -> SHORTLISTED",
                ApplicationService.canTransition("ACCEPTED", "SHORTLISTED"));
        assertFalse("ACCEPTED -> IN_REVIEW",
                ApplicationService.canTransition("ACCEPTED", "IN_REVIEW"));
        assertFalse("REJECTED -> SUBMITTED",
                ApplicationService.canTransition("REJECTED", "SUBMITTED"));
    }

    // ==================== normalizeStatus 测试 ====================

    /**
     * normalizeStatus 标准化状态字符串。
     * normalizeStatus 是私有方法，通过 isValidStatus 间接验证其行为：
     * - null 和空字符串默认转为 "SUBMITTED"（合法状态）
     * - 带空格的字符串会被 trim 并转大写
     */
    public void testNormalizeStatusNormalisesStatusString() {
        // null 默认为 "SUBMITTED"，是合法状态
        assertTrue("null should normalize to SUBMITTED (valid)",
                ApplicationService.isValidStatus(null));
        // 空字符串默认为 "SUBMITTED"，是合法状态
        assertTrue("empty string should normalize to SUBMITTED (valid)",
                ApplicationService.isValidStatus(""));
        // 两端空格去除 + 转大写后匹配
        assertTrue("'  submitted  ' should normalize to SUBMITTED",
                ApplicationService.isValidStatus("  submitted  "));
        assertTrue("'  In_Review  ' should normalize to IN_REVIEW",
                ApplicationService.isValidStatus("  In_Review  "));
        // 无法匹配任何合法状态的随机字符串
        assertFalse("random string should be invalid after normalization",
                ApplicationService.isValidStatus("  random_thing  "));
    }

    // ---- main ----

    public static void main(String[] args) {
        new ApplicationServiceTest().runTests();
    }
}
