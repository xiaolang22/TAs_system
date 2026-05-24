package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.TimelineEvent;

/**
 * Unit tests for the TimelineEvent model class.
 * Tests default constructor and all 5 getter/setter pairs.
 *
 * @author Group19
 * @since 1.0
 */
public class TimelineEventTest extends TestRunner {

    public void testDefaultConstructorInitialisesAllFieldsToNull() {
        TimelineEvent event = new TimelineEvent();
        assertNotNull("Default constructor should create a non-null TimelineEvent object", event);
        assertNull("eventId should be null after default constructor", event.getEventId());
        assertNull("applicationId should be null after default constructor", event.getApplicationId());
        assertNull("stage should be null after default constructor", event.getStage());
        assertNull("occurredAt should be null after default constructor", event.getOccurredAt());
        assertNull("note should be null after default constructor", event.getNote());
    }

    public void testSetAndGetEventId() {
        TimelineEvent event = new TimelineEvent();
        event.setEventId("EVT001");
        assertEquals("EVT001", event.getEventId());
    }

    public void testSetAndGetApplicationId() {
        TimelineEvent event = new TimelineEvent();
        event.setApplicationId("APP001");
        assertEquals("APP001", event.getApplicationId());
    }

    public void testSetAndGetStage() {
        TimelineEvent event = new TimelineEvent();
        event.setStage("submitted");
        assertEquals("submitted", event.getStage());
    }

    public void testSetAndGetOccurredAt() {
        TimelineEvent event = new TimelineEvent();
        event.setOccurredAt("2024-01-15T10:30:00");
        assertEquals("2024-01-15T10:30:00", event.getOccurredAt());
    }

    public void testSetAndGetNote() {
        TimelineEvent event = new TimelineEvent();
        event.setNote("申请已成功提交");
        assertEquals("申请已成功提交", event.getNote());
    }

    public void testAllFieldsSetAndGetConsistently() {
        TimelineEvent event = new TimelineEvent();
        event.setEventId("EVT002");
        event.setApplicationId("APP002");
        event.setStage("under_review");
        event.setOccurredAt("2024-02-01T14:00:00");
        event.setNote("申请正在审核中");

        assertEquals("EVT002", event.getEventId());
        assertEquals("APP002", event.getApplicationId());
        assertEquals("under_review", event.getStage());
        assertEquals("2024-02-01T14:00:00", event.getOccurredAt());
        assertEquals("申请正在审核中", event.getNote());
    }

    public static void main(String[] args) {
        new TimelineEventTest().runTestsAndExit();
    }
}
