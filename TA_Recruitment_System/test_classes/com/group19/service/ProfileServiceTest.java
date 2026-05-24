package com.group19.service;

import com.group19.TestRunner;
import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.TA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * unit test：ProfileService 个人资料服务
 * 使用临时 JSON 文件作为 TADao 的数据源。
 *
 * @author Group19
 */
public class ProfileServiceTest extends TestRunner {

    private Path tempFile;
    private TADao taDao;
    private ProfileService profileService;

    /**
     * 创建临时 JSON 文件，写入空数组。
     */
    private static Path createTempJsonFile() throws IOException {
        Path tempFile = Files.createTempFile("test-ta-profiles-", ".json");
        Files.writeString(tempFile, "[]");
        return tempFile;
    }

    /**
     * 每个测试方法执行前：创建临时文件并初始化 DAO 和服务。
     */
    @Override
    protected void setUp() throws Exception {
        tempFile = createTempJsonFile();
        taDao = new TADao(tempFile);
        profileService = new ProfileService(taDao);
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

    /** 保存一份有效资料作为测试前置条件。 */
    private void saveValidProfile(String studentId, String name) {
        profileService.saveProfile(
                name, studentId, "test@example.com",
                "Computer Science", "Java, Python", "2 years experience",
                "Mon-Fri 9am-5pm");
    }

    // ==================== getProfileByStudentId 测试 ====================

    /** 根据已存在的学号获取资料应成功。 */
    public void testGetProfileByStudentIdReturnsProfileForExistingStudent() {
        saveValidProfile("STU001", "Alice Wang");
        ServiceResult<TA> result = profileService.getProfileByStudentId("STU001");
        assertTrue("Should find existing profile: " + result.getMessage(), result.isSuccess());
        assertNotNull("TA should not be null", result.getData());
        assertEquals("Name should match", "Alice Wang", result.getData().getName());
    }

    /** 学号为空时获取资料应失败。 */
    public void testGetProfileByStudentIdFailsForEmptyStudentId() {
        ServiceResult<TA> result = profileService.getProfileByStudentId("");
        assertFalse("Empty student ID should fail", result.isSuccess());
    }

    // ==================== saveProfile 测试 ====================

    /** 有效资料保存应成功。 */
    public void testSaveProfileWithValidData() {
        ServiceResult<TA> result = profileService.saveProfile(
                "Bob Chen", "STU002", "bob@example.com",
                "Mathematics", "Statistics, R", "1 year tutoring",
                "Tue-Thu 10am-2pm");
        assertTrue("Valid profile save should succeed: " + result.getMessage(), result.isSuccess());
        assertNotNull("Saved TA should not be null", result.getData());
        assertEquals("Name should match", "Bob Chen", result.getData().getName());
    }

    /** 姓名为空时保存资料应失败。 */
    public void testSaveProfileWithEmptyName() {
        ServiceResult<TA> result = profileService.saveProfile(
                "", "STU003", "test@example.com",
                "CS", "Java", "Exp", "Mon-Fri");
        assertFalse("Empty name should fail", result.isSuccess());
    }

    /** 邮箱格式无效时保存资料应失败。 */
    public void testSaveProfileWithInvalidEmail() {
        ServiceResult<TA> result = profileService.saveProfile(
                "Test User", "STU004", "not-an-email",
                "CS", "Java", "Exp", "Mon-Fri");
        assertFalse("Invalid email should fail", result.isSuccess());
    }

    /** 技能为空时保存资料应失败。 */
    public void testSaveProfileWithEmptySkills() {
        ServiceResult<TA> result = profileService.saveProfile(
                "Test User", "STU005", "test@example.com",
                "CS", "", "Exp", "Mon-Fri");
        assertFalse("Empty skills should fail", result.isSuccess());
    }

    /** 重复保存同一学号应更新已有资料（而非新增）。 */
    public void testSaveProfileUpdatesExistingProfile() {
        // 第一次保存
        profileService.saveProfile(
                "Original Name", "STU006", "orig@example.com",
                "Physics", "Lab Skills", "None", "Mon-Wed");
        // 第二次用同一学号保存
        ServiceResult<TA> result = profileService.saveProfile(
                "Updated Name", "STU006", "updated@example.com",
                "Physics", "Advanced Lab", "2 years", "Mon-Wed");
        assertTrue("Update should succeed", result.isSuccess());

        // 验证获取到的是更新后的资料
        ServiceResult<TA> fetched = profileService.getProfileByStudentId("STU006");
        assertTrue("Should find updated profile", fetched.isSuccess());
        assertEquals("Name should be updated", "Updated Name", fetched.getData().getName());
        assertEquals("Email should be updated", "updated@example.com", fetched.getData().getEmail());
    }

    // ---- main ----

    public static void main(String[] args) {
        new ProfileServiceTest().runTests();
    }
}
