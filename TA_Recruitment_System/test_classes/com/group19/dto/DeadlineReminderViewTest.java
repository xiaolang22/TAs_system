package com.group19.dto;

import com.group19.TestRunner;

/**
 * Unit tests for {@link DeadlineReminderView}.
 *
 * @author Group19
 */
public class DeadlineReminderViewTest extends TestRunner {

    // ---- 5 getter / setter pairs ----

    // 1. jobTitle
    public void testSetAndGetJobTitle() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setJobTitle("算法课助教");
        assertEquals("算法课助教", view.getJobTitle());
    }

    // 2. deadlineDisplay
    public void testSetAndGetDeadlineDisplay() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setDeadlineDisplay("2026-06-15");
        assertEquals("2026-06-15", view.getDeadlineDisplay());
    }

    // 3. daysLabel
    public void testSetAndGetDaysLabel() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setDaysLabel("3天后截止");
        assertEquals("3天后截止", view.getDaysLabel());
    }

    // 4. reminderClass
    public void testSetAndGetReminderClass() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setReminderClass("urgent");
        assertEquals("urgent", view.getReminderClass());
    }

    // 5. actionUrl
    public void testSetAndGetActionUrl() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setActionUrl("/jobs/detail?id=42");
        assertEquals("/jobs/detail?id=42", view.getActionUrl());
    }

    // ---- Integration tests ----

    public void testSetAllFieldsThenGetAll() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setJobTitle("数据结构课助教");
        view.setDeadlineDisplay("2026-07-01");
        view.setDaysLabel("7天后截止");
        view.setReminderClass("warning");
        view.setActionUrl("/jobs/apply?id=100");

        assertEquals("数据结构课助教", view.getJobTitle());
        assertEquals("2026-07-01", view.getDeadlineDisplay());
        assertEquals("7天后截止", view.getDaysLabel());
        assertEquals("warning", view.getReminderClass());
        assertEquals("/jobs/apply?id=100", view.getActionUrl());
    }

    public void testOverwriteAllFields() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setJobTitle("旧职位");
        view.setDeadlineDisplay("2026-05-01");
        view.setDaysLabel("旧标签");
        view.setReminderClass("normal");
        view.setActionUrl("/old/url");

        view.setJobTitle("新职位");
        view.setDeadlineDisplay("2026-08-01");
        view.setDaysLabel("新标签");
        view.setReminderClass("urgent");
        view.setActionUrl("/new/url");

        assertEquals("新职位", view.getJobTitle());
        assertEquals("2026-08-01", view.getDeadlineDisplay());
        assertEquals("新标签", view.getDaysLabel());
        assertEquals("urgent", view.getReminderClass());
        assertEquals("/new/url", view.getActionUrl());
    }

    public void testNullValues() {
        DeadlineReminderView view = new DeadlineReminderView();
        view.setJobTitle(null);
        view.setDeadlineDisplay(null);
        view.setDaysLabel(null);
        view.setReminderClass(null);
        view.setActionUrl(null);

        assertNull(view.getJobTitle());
        assertNull(view.getDeadlineDisplay());
        assertNull(view.getDaysLabel());
        assertNull(view.getReminderClass());
        assertNull(view.getActionUrl());
    }

    public static void main(String[] args) {
        new DeadlineReminderViewTest().runTests();
    }
}
