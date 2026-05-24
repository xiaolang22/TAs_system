package com.group19.service;

import com.group19.TestRunner;
import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.dto.TaWorkloadRow;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.TA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * unit test：WorkloadService 工作负载服务
 * 使用临时 JSON 文件作为 TADao、ApplicationDao 和 JobDao 的数据源。
 *
 * @author Group19
 */
public class WorkloadServiceTest extends TestRunner {

    private Path taTempFile;
    private Path appTempFile;
    private Path jobTempFile;
    private TADao taDao;
    private ApplicationDao applicationDao;
    private JobDao jobDao;
    private WorkloadService workloadService;

    /**
     * 创建临时 JSON 文件，写入空数组。
     */
    private static Path createTempJsonFile() throws IOException {
        Path tempFile = Files.createTempFile("test-workload-", ".json");
        Files.writeString(tempFile, "[]");
        return tempFile;
    }

    /**
     * 每个测试方法执行前：创建临时文件并初始化所有 DAO 和服务。
     */
    @Override
    protected void setUp() throws Exception {
        taTempFile = createTempJsonFile();
        appTempFile = createTempJsonFile();
        jobTempFile = createTempJsonFile();
        taDao = new TADao(taTempFile);
        applicationDao = new ApplicationDao(appTempFile);
        jobDao = new JobDao(jobTempFile);
        workloadService = new WorkloadService(taDao, applicationDao, jobDao);
    }

    /**
     * 每个测试方法执行后：删除临时文件。
     */
    @Override
    protected void tearDown() throws Exception {
        if (taTempFile != null) {
            Files.deleteIfExists(taTempFile);
        }
        if (appTempFile != null) {
            Files.deleteIfExists(appTempFile);
        }
        if (jobTempFile != null) {
            Files.deleteIfExists(jobTempFile);
        }
    }

    // ---- Helper methods ----

    /** 创建并持久化一个 TA 记录。 */
    private TA createTA(String studentId, String name, String email, String programme) throws IOException {
        TA ta = new TA(name, studentId, email, programme, "Java, Python", "Mon-Fri");
        ta.setExperience("2 years");
        ta.setUpdatedAt("2026-05-24 10:00:00");
        return taDao.saveOrUpdate(ta);
    }

    /** 创建并持久化一个 Job 记录。 */
    private Job createJob(String jobId, String title, String hours) {
        Job job = new Job();
        job.setJobId(jobId);
        job.setTitle(title);
        job.setCategory("Grad");
        job.setDescription("Test job description");
        job.setRequirements("Java, SQL");
        job.setHours(hours);
        job.setSchedule("Mon, Wed 10am-12pm");
        job.setDeadline("2027-12-31");
        job.setStatus("OPEN");
        job.setCreatedAt("2026-05-24T10:00:00");
        job.setOwnerMoUserId("MO001");
        jobDao.save(job);
        return job;
    }

    /** 创建并持久化一个已接受的 Application 记录。 */
    private Application createAcceptedApplication(String jobId, String taStudentId, String taName) {
        Application app = new Application();
        app.setApplicationId(UUID.randomUUID().toString());
        app.setJobId(jobId);
        app.setTaStudentId(taStudentId);
        app.setTaName(taName);
        app.setCvFilePath("/uploads/cv.pdf");
        app.setStatus("ACCEPTED");
        app.setSubmittedAt("2026-05-24T09:00:00");
        app.setUpdatedAt("2026-05-24T10:00:00");
        app.setDecisionNote("");
        applicationDao.save(app);
        return app;
    }

    // ==================== 构造函数测试 ====================

    /** 构造函数应成功创建实例。 */
    public void testConstructorCreatesInstance() {
        assertNotNull("WorkloadService should be created", workloadService);
    }

    /** 自定义最大周工时构造函数应成功创建实例。 */
    public void testConstructorWithCustomMaxHours() {
        WorkloadService ws = new WorkloadService(taDao, applicationDao, jobDao, 10);
        assertNotNull("WorkloadService with custom max hours should be created", ws);
    }

    // ==================== loadWorkloadRows 测试 ====================

    /** 无数据时 loadWorkloadRows 应返回空列表。 */
    public void testLoadWorkloadRowsReturnsEmptyListWhenNoData() {
        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows(null, null);
        assertTrue("Should succeed with empty data: " + result.getMessage(), result.isSuccess());
        assertNotNull("Result data should not be null", result.getData());
        assertTrue("Rows should be empty", result.getData().isEmpty());
    }

    /** 存在已接受申请时，应返回包含该 TA 和职位的负载行。 */
    public void testLoadWorkloadRowsIncludesTaWithAcceptedApplication() throws IOException {
        // 准备测试数据
        createTA("STU001", "Alice Wang", "alice@example.com", "Computer Science");
        createJob("JOB-001", "TA Lab Assistant", "10 hours/week");
        createAcceptedApplication("JOB-001", "STU001", "Alice Wang");

        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows(null, null);
        assertTrue("Should succeed: " + result.getMessage(), result.isSuccess());

        List<TaWorkloadRow> rows = result.getData();
        assertTrue("Should have at least one row", rows.size() > 0);

        // 查找 Alice 所在行
        TaWorkloadRow aliceRow = null;
        for (TaWorkloadRow row : rows) {
            if ("Alice Wang".equals(row.getName())) {
                aliceRow = row;
                break;
            }
        }
        assertNotNull("Alice should have a workload row", aliceRow);
        assertEquals("Alice should have 1 assigned position", 1, aliceRow.getAssignedPositionCount());
        assertTrue("Total assigned hours should be > 0", aliceRow.getTotalAssignedHours() > 0);
    }

    /** 按关键字过滤应只返回匹配的 TA。 */
    public void testLoadWorkloadRowsFiltersByKeyword() throws IOException {
        createTA("STU001", "Alice Wang", "alice@example.com", "CS");
        createTA("STU002", "Bob Chen", "bob@example.com", "Math");
        createJob("JOB-001", "TA Position", "8 hours/week");
        createAcceptedApplication("JOB-001", "STU001", "Alice Wang");

        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows("Alice", null);
        assertTrue("Should succeed: " + result.getMessage(), result.isSuccess());

        List<TaWorkloadRow> rows = result.getData();
        assertEquals("Should filter to 1 row matching 'Alice'", 1, rows.size());
        assertEquals("Name should be Alice Wang", "Alice Wang", rows.get(0).getName());
    }

    /** assigned 过滤器应只返回已有分配的 TA。 */
    public void testLoadWorkloadRowsFiltersByAssigned() throws IOException {
        createTA("STU001", "Alice Wang", "alice@example.com", "CS");
        createTA("STU002", "Bob Chen", "bob@example.com", "Math");
        createJob("JOB-001", "TA Position", "8 hours/week");
        createAcceptedApplication("JOB-001", "STU001", "Alice Wang");

        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows(null, "assigned");
        assertTrue("Should succeed: " + result.getMessage(), result.isSuccess());

        List<TaWorkloadRow> rows = result.getData();
        assertTrue("Should have at least one row", rows.size() > 0);
        for (TaWorkloadRow row : rows) {
            assertTrue("All rows should have assigned positions",
                    row.getAssignedPositionCount() > 0);
        }
    }

    /** unassigned 过滤器应只返回无分配的 TA。 */
    public void testLoadWorkloadRowsFiltersByUnassigned() throws IOException {
        createTA("STU001", "Alice Wang", "alice@example.com", "CS");
        createTA("STU002", "Bob Chen", "bob@example.com", "Math");
        createJob("JOB-001", "TA Position", "8 hours/week");
        // 只有 Alice 有申请
        createAcceptedApplication("JOB-001", "STU001", "Alice Wang");

        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows(null, "unassigned");
        assertTrue("Should succeed: " + result.getMessage(), result.isSuccess());

        List<TaWorkloadRow> rows = result.getData();
        assertTrue("Should have at least one row (Bob should be unassigned)",
                rows.size() > 0);
        for (TaWorkloadRow row : rows) {
            assertEquals("All rows should have 0 assigned positions",
                    0, row.getAssignedPositionCount());
        }
    }

    /** 存在非已接受状态（如 SUBMITTED）的申请时，不应计入负载。 */
    public void testLoadWorkloadRowsExcludesNonAcceptedApplications() throws IOException {
        createTA("STU001", "Alice Wang", "alice@example.com", "CS");
        createJob("JOB-001", "TA Position", "10 hours/week");

        // 创建 SUBMITTED 状态的申请（非 ACCEPTED）
        Application app = new Application();
        app.setApplicationId(UUID.randomUUID().toString());
        app.setJobId("JOB-001");
        app.setTaStudentId("STU001");
        app.setTaName("Alice Wang");
        app.setCvFilePath("/uploads/cv.pdf");
        app.setStatus("SUBMITTED");
        app.setSubmittedAt("2026-05-24T09:00:00");
        app.setUpdatedAt("2026-05-24T10:00:00");
        app.setDecisionNote("");
        applicationDao.save(app);

        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows(null, null);
        assertTrue("Should succeed: " + result.getMessage(), result.isSuccess());

        // 查找 Alice
        TaWorkloadRow aliceRow = null;
        for (TaWorkloadRow row : result.getData()) {
            if ("Alice Wang".equals(row.getName())) {
                aliceRow = row;
                break;
            }
        }
        assertNotNull("Alice should have a row", aliceRow);
        assertEquals("Alice should have 0 assigned positions (non-accepted)",
                0, aliceRow.getAssignedPositionCount());
    }

    // ---- main ----

    public static void main(String[] args) {
        new WorkloadServiceTest().runTests();
    }
}
