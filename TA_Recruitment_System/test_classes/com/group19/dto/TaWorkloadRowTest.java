package com.group19.dto;

import com.group19.TestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link TaWorkloadRow}.
 *
 * @author Group19
 */
public class TaWorkloadRowTest extends TestRunner {

    // ---- 9 getter / setter pairs ----

    // 1. studentId
    public void testSetAndGetStudentId() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setStudentId("2024001");
        assertEquals("2024001", row.getStudentId());
    }

    // 2. name
    public void testSetAndGetName() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setName("Alice Johnson");
        assertEquals("Alice Johnson", row.getName());
    }

    // 3. email
    public void testSetAndGetEmail() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setEmail("zhangsan@university.edu.cn");
        assertEquals("zhangsan@university.edu.cn", row.getEmail());
    }

    // 4. programme
    public void testSetAndGetProgramme() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setProgramme("计算机科学");
        assertEquals("计算机科学", row.getProgramme());
    }

    // 5. assignedPositions
    public void testSetAndGetAssignedPositions() {
        TaWorkloadRow row = new TaWorkloadRow();
        AssignedPositionDto pos1 = new AssignedPositionDto();
        pos1.setJobTitle("算法课助教");
        AssignedPositionDto pos2 = new AssignedPositionDto();
        pos2.setJobTitle("数据结构课助教");
        List<AssignedPositionDto> positions = Arrays.asList(pos1, pos2);

        row.setAssignedPositions(positions);
        List<AssignedPositionDto> returned = row.getAssignedPositions();

        assertNotNull("returned list should not be null", returned);
        assertEquals(2, returned.size());
        assertEquals("算法课助教", returned.get(0).getJobTitle());
        assertEquals("数据结构课助教", returned.get(1).getJobTitle());
    }

    public void testSetAssignedPositionsWithNullCreatesEmptyList() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setAssignedPositions(Arrays.asList(new AssignedPositionDto()));
        row.setAssignedPositions(null);

        List<AssignedPositionDto> returned = row.getAssignedPositions();
        assertNotNull("should not return null after being set to null", returned);
        assertTrue("should return empty list after being set to null", returned.isEmpty());
    }

    // 6. assignedPositionCount
    public void testSetAndGetAssignedPositionCount() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setAssignedPositionCount(3);
        assertEquals(3, row.getAssignedPositionCount());
    }

    // 7. totalAssignedHours (double)
    public void testSetAndGetTotalAssignedHours() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setTotalAssignedHours(15.5);
        assertEquals(15.5, row.getTotalAssignedHours(), 0.001);
    }

    public void testSetAndGetTotalAssignedHoursZero() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setTotalAssignedHours(0.0);
        assertEquals(0.0, row.getTotalAssignedHours(), 0.001);
    }

    // 8. totalAssignedHoursLabel
    public void testSetAndGetTotalAssignedHoursLabel() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setTotalAssignedHoursLabel("15.5h");
        assertEquals("15.5h", row.getTotalAssignedHoursLabel());
    }

    // 9. workloadWarningReasons
    public void testSetAndGetWorkloadWarningReasons() {
        TaWorkloadRow row = new TaWorkloadRow();
        List<String> reasons = Arrays.asList("工作量超限", "超过最大小时数");
        row.setWorkloadWarningReasons(reasons);

        List<String> returned = row.getWorkloadWarningReasons();
        assertNotNull("returned list should not be null", returned);
        assertEquals(2, returned.size());
        assertEquals("工作量超限", returned.get(0));
        assertEquals("超过最大小时数", returned.get(1));
    }

    public void testSetWorkloadWarningReasonsWithNullCreatesEmptyList() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setWorkloadWarningReasons(Arrays.asList("警告1"));
        row.setWorkloadWarningReasons(null);

        List<String> returned = row.getWorkloadWarningReasons();
        assertNotNull("should not return null after being set to null", returned);
        assertTrue("should return empty list after being set to null", returned.isEmpty());
    }

    // ---- isHasWorkloadWarning() ----

    public void testIsHasWorkloadWarningReturnsFalseForEmptyList() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setWorkloadWarningReasons(Collections.emptyList());
        assertFalse("isHasWorkloadWarning() should be false for empty list", row.isHasWorkloadWarning());
    }

    public void testIsHasWorkloadWarningReturnsFalseForDefault() {
        TaWorkloadRow row = new TaWorkloadRow();
        // Default workloadWarningReasons is a new ArrayList, which is empty
        assertFalse("isHasWorkloadWarning() should be false for default empty list", row.isHasWorkloadWarning());
    }

    public void testIsHasWorkloadWarningReturnsTrueForNonEmptyList() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setWorkloadWarningReasons(Arrays.asList("警告"));
        assertTrue("isHasWorkloadWarning() should return true for non-empty list", row.isHasWorkloadWarning());
    }

    public void testIsHasWorkloadWarningReturnsTrueForMultipleReasons() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setWorkloadWarningReasons(Arrays.asList("原因1", "原因2", "原因3"));
        assertTrue("isHasWorkloadWarning() should return true for multiple warnings", row.isHasWorkloadWarning());
    }

    // ---- Integration tests ----

    public void testSetAllFieldsThenGetAll() {
        TaWorkloadRow row = new TaWorkloadRow();
        row.setStudentId("2024001");
        row.setName("Alice Johnson");
        row.setEmail("zhangsan@university.edu.cn");
        row.setProgramme("计算机科学");
        row.setAssignedPositionCount(2);
        row.setTotalAssignedHours(20.0);
        row.setTotalAssignedHoursLabel("20.0h");

        AssignedPositionDto pos = new AssignedPositionDto();
        pos.setJobTitle("算法课助教");
        row.setAssignedPositions(Arrays.asList(pos));

        row.setWorkloadWarningReasons(Arrays.asList("总时长超限"));

        assertEquals("2024001", row.getStudentId());
        assertEquals("Alice Johnson", row.getName());
        assertEquals("zhangsan@university.edu.cn", row.getEmail());
        assertEquals("计算机科学", row.getProgramme());
        assertEquals(2, row.getAssignedPositionCount());
        assertEquals(20.0, row.getTotalAssignedHours(), 0.001);
        assertEquals("20.0h", row.getTotalAssignedHoursLabel());
        assertEquals(1, row.getAssignedPositions().size());
        assertEquals(1, row.getWorkloadWarningReasons().size());
        assertTrue("should have workload warning", row.isHasWorkloadWarning());
    }

    public void testWorkloadWarningStateTransitions() {
        TaWorkloadRow row = new TaWorkloadRow();

        // Initial state: no warnings
        assertFalse(row.isHasWorkloadWarning());

        // Add a warning
        row.setWorkloadWarningReasons(Arrays.asList("警告"));
        assertTrue(row.isHasWorkloadWarning());

        // Remove warnings (set to empty list)
        row.setWorkloadWarningReasons(Collections.emptyList());
        assertFalse(row.isHasWorkloadWarning());

        // Re-add
        row.setWorkloadWarningReasons(Arrays.asList("新警告"));
        assertTrue(row.isHasWorkloadWarning());

        // Set to null -> empty list
        row.setWorkloadWarningReasons(null);
        assertFalse(row.isHasWorkloadWarning());
    }

    public static void main(String[] args) {
        new TaWorkloadRowTest().runTests();
    }
}
