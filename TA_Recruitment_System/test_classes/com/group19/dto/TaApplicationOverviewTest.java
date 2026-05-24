package com.group19.dto;

import com.group19.TestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link TaApplicationOverview}.
 *
 * @author Group19
 */
public class TaApplicationOverviewTest extends TestRunner {

    // ---- 6 string getter / setter pairs ----

    public void testSetAndGetApplicationId() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setApplicationId("APP-001");
        assertEquals("APP-001", overview.getApplicationId());
    }

    public void testSetAndGetJobId() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setJobId("JOB-042");
        assertEquals("JOB-042", overview.getJobId());
    }

    public void testSetAndGetJobTitle() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setJobTitle("算法课助教");
        assertEquals("算法课助教", overview.getJobTitle());
    }

    public void testSetAndGetStatusLabel() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setStatusLabel("审核中");
        assertEquals("审核中", overview.getStatusLabel());
    }

    public void testSetAndGetStatusPillClass() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setStatusPillClass("badge-warning");
        assertEquals("badge-warning", overview.getStatusPillClass());
    }

    public void testSetAndGetLastUpdatedDisplay() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setLastUpdatedDisplay("2026-05-20 14:30");
        assertEquals("2026-05-20 14:30", overview.getLastUpdatedDisplay());
    }

    // ---- timelineSteps list getter / setter ----

    public void testDefaultTimelineStepsIsEmptyList() {
        TaApplicationOverview overview = new TaApplicationOverview();
        List<TaTimelineStep> steps = overview.getTimelineSteps();
        assertNotNull("default timelineSteps should not be null", steps);
        assertTrue("default timelineSteps should be empty list", steps.isEmpty());
    }

    public void testSetAndGetTimelineSteps() {
        TaApplicationOverview overview = new TaApplicationOverview();
        TaTimelineStep step1 = new TaTimelineStep();
        step1.setLabel("已提交申请");
        TaTimelineStep step2 = new TaTimelineStep();
        step2.setLabel("审核中");
        List<TaTimelineStep> steps = Arrays.asList(step1, step2);

        overview.setTimelineSteps(steps);
        List<TaTimelineStep> returned = overview.getTimelineSteps();

        assertNotNull("returned list should not be null", returned);
        assertEquals(2, returned.size());
        assertEquals("已提交申请", returned.get(0).getLabel());
        assertEquals("审核中", returned.get(1).getLabel());
    }

    // ---- setTimelineSteps handling null ----

    public void testSetTimelineStepsWithNullCreatesEmptyList() {
        TaApplicationOverview overview = new TaApplicationOverview();
        // First set a non-empty list
        overview.setTimelineSteps(Arrays.asList(new TaTimelineStep()));

        // Then set to null
        overview.setTimelineSteps(null);
        List<TaTimelineStep> returned = overview.getTimelineSteps();

        assertNotNull("should not return null after being set to null", returned);
        assertTrue("should return empty list after being set to null", returned.isEmpty());
    }

    // ---- Integration tests ----

    public void testSetAllFieldsThenGetAll() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setApplicationId("APP-002");
        overview.setJobId("JOB-100");
        overview.setJobTitle("数据结构课助教");
        overview.setStatusLabel("已通过");
        overview.setStatusPillClass("badge-success");
        overview.setLastUpdatedDisplay("2026-05-24 09:00");

        TaTimelineStep step = new TaTimelineStep();
        step.setLabel("已录用");
        overview.setTimelineSteps(Arrays.asList(step));

        assertEquals("APP-002", overview.getApplicationId());
        assertEquals("JOB-100", overview.getJobId());
        assertEquals("数据结构课助教", overview.getJobTitle());
        assertEquals("已通过", overview.getStatusLabel());
        assertEquals("badge-success", overview.getStatusPillClass());
        assertEquals("2026-05-24 09:00", overview.getLastUpdatedDisplay());
        assertEquals(1, overview.getTimelineSteps().size());
        assertEquals("已录用", overview.getTimelineSteps().get(0).getLabel());
    }

    public void testTimelineStepsListIsMutable() {
        TaApplicationOverview overview = new TaApplicationOverview();
        overview.setTimelineSteps(new ArrayList<>());

        TaTimelineStep step = new TaTimelineStep();
        step.setLabel("新步骤");
        overview.getTimelineSteps().add(step);

        assertEquals(1, overview.getTimelineSteps().size());
        assertEquals("新步骤", overview.getTimelineSteps().get(0).getLabel());
    }

    public static void main(String[] args) {
        new TaApplicationOverviewTest().runTests();
    }
}
