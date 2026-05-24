package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.Job;

/**
 * Unit tests for the Job model class.
 * Tests default constructor and all 11 getter/setter pairs.
 *
 * @author Group19
 * @since 1.0
 */
public class JobTest extends TestRunner {

    public void testDefaultConstructorInitialisesAllFieldsToNull() {
        Job job = new Job();
        assertNotNull("Default constructor should create a non-null Job object", job);
        assertNull("jobId should be null after default constructor", job.getJobId());
        assertNull("title should be null after default constructor", job.getTitle());
        assertNull("category should be null after default constructor", job.getCategory());
        assertNull("description should be null after default constructor", job.getDescription());
        assertNull("requirements should be null after default constructor", job.getRequirements());
        assertNull("hours should be null after default constructor", job.getHours());
        assertNull("schedule should be null after default constructor", job.getSchedule());
        assertNull("deadline should be null after default constructor", job.getDeadline());
        assertNull("status should be null after default constructor", job.getStatus());
        assertNull("createdAt should be null after default constructor", job.getCreatedAt());
        assertNull("ownerMoUserId should be null after default constructor", job.getOwnerMoUserId());
    }

    public void testSetAndGetJobId() {
        Job job = new Job();
        job.setJobId("JOB001");
        assertEquals("JOB001", job.getJobId());
    }

    public void testSetAndGetTitle() {
        Job job = new Job();
        job.setTitle("助教招募");
        assertEquals("助教招募", job.getTitle());
    }

    public void testSetAndGetCategory() {
        Job job = new Job();
        job.setCategory("Grad");
        assertEquals("Grad", job.getCategory());
    }

    public void testSetAndGetDescription() {
        Job job = new Job();
        job.setDescription("需要一名软件工程课程助教");
        assertEquals("需要一名软件工程课程助教", job.getDescription());
    }

    public void testSetAndGetRequirements() {
        Job job = new Job();
        job.setRequirements("熟悉Java编程，有良好的沟通能力");
        assertEquals("熟悉Java编程，有良好的沟通能力", job.getRequirements());
    }

    public void testSetAndGetHours() {
        Job job = new Job();
        job.setHours("20");
        assertEquals("20", job.getHours());
    }

    public void testSetAndGetSchedule() {
        Job job = new Job();
        job.setSchedule("周一至周五 9:00-17:00");
        assertEquals("周一至周五 9:00-17:00", job.getSchedule());
    }

    public void testSetAndGetDeadline() {
        Job job = new Job();
        job.setDeadline("2024-03-01");
        assertEquals("2024-03-01", job.getDeadline());
    }

    public void testSetAndGetStatus() {
        Job job = new Job();
        job.setStatus("open");
        assertEquals("open", job.getStatus());
    }

    public void testSetAndGetCreatedAt() {
        Job job = new Job();
        job.setCreatedAt("2024-01-10T08:00:00");
        assertEquals("2024-01-10T08:00:00", job.getCreatedAt());
    }

    public void testSetAndGetOwnerMoUserId() {
        Job job = new Job();
        job.setOwnerMoUserId("MO001");
        assertEquals("MO001", job.getOwnerMoUserId());
    }

    public void testAllFieldsSetAndGetConsistently() {
        Job job = new Job();
        job.setJobId("JOB002");
        job.setTitle("高级助教");
        job.setCategory("PhD");
        job.setDescription("需要一名博士研究生担任高级助教");
        job.setRequirements("精通Python和机器学习");
        job.setHours("15");
        job.setSchedule("周三和周五 14:00-18:00");
        job.setDeadline("2024-04-15");
        job.setStatus("closed");
        job.setCreatedAt("2024-02-01T10:00:00");
        job.setOwnerMoUserId("MO002");

        assertEquals("JOB002", job.getJobId());
        assertEquals("高级助教", job.getTitle());
        assertEquals("PhD", job.getCategory());
        assertEquals("需要一名博士研究生担任高级助教", job.getDescription());
        assertEquals("精通Python和机器学习", job.getRequirements());
        assertEquals("15", job.getHours());
        assertEquals("周三和周五 14:00-18:00", job.getSchedule());
        assertEquals("2024-04-15", job.getDeadline());
        assertEquals("closed", job.getStatus());
        assertEquals("2024-02-01T10:00:00", job.getCreatedAt());
        assertEquals("MO002", job.getOwnerMoUserId());
    }

    public static void main(String[] args) {
        new JobTest().runTestsAndExit();
    }
}
