package com.group19.dao;

import com.group19.TestRunner;
import com.group19.model.TimelineEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for TimelineDao. Uses temporary JSON files for data read/write verification.
 *
 * @author Group19
 * @since 1.0
 */
public class TimelineDaoTest extends TestRunner {

    private Path tempFile;
    private TimelineDao dao;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("test-timeline-", ".json");
        Files.writeString(tempFile, "[]");
        dao = new TimelineDao(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    // ---------- findAll ----------

    /** findAll() returns an empty list initially. */
    public void testFindAllReturnsEmptyInitially() {
        List<TimelineEvent> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertTrue("initial list should be empty", result.isEmpty());
    }

    // ---------- append ----------

    /** append() adds an event. */
    public void testAppendAddsAnEvent() {
        TimelineEvent event = createEvent("EVT-001", "APP-001", "submitted",
                "2025-01-01 10:00:00", "申请已提交");
        boolean appended = dao.append(event);
        assertTrue("append() should return true", appended);

        List<TimelineEvent> all = dao.findAll();
        assertEquals("one record should exist after append", 1, all.size());
        assertEquals("EVT-001", all.get(0).getEventId());
    }

    // ---------- findByApplicationIdOrdered ----------

    /** findByApplicationIdOrdered() returns events sorted by time. */
    public void testFindByApplicationIdOrderedReturnsEventsSortedByTime() {
        // Insert deliberately out of order
        dao.append(createEvent("EVT-003", "APP-001", "approved",
                "2025-03-01 10:00:00", "审核通过"));
        dao.append(createEvent("EVT-001", "APP-001", "submitted",
                "2025-01-01 10:00:00", "申请已提交"));
        dao.append(createEvent("EVT-002", "APP-001", "under_review",
                "2025-02-01 10:00:00", "进入审核"));

        List<TimelineEvent> ordered = dao.findByApplicationIdOrdered("APP-001");
        assertEquals("should have three events", 3, ordered.size());
        assertEquals("first should be the earliest (submitted)", "EVT-001", ordered.get(0).getEventId());
        assertEquals("second should be under_review", "EVT-002", ordered.get(1).getEventId());
        assertEquals("third should be approved", "EVT-003", ordered.get(2).getEventId());
    }

    /** findByApplicationIdOrdered() filters by applicationId. */
    public void testFindByApplicationIdOrderedFiltersByApplicationId() {
        dao.append(createEvent("EVT-001", "APP-001", "submitted",
                "2025-01-01 10:00:00", "APP-001 事件"));
        dao.append(createEvent("EVT-002", "APP-002", "submitted",
                "2025-01-01 10:00:00", "APP-002 事件"));

        List<TimelineEvent> result = dao.findByApplicationIdOrdered("APP-001");
        assertEquals("APP-001 should have only one event", 1, result.size());
        assertEquals("EVT-001", result.get(0).getEventId());
    }

    /** findByApplicationIdOrdered() returns an empty list for a non-existent ID. */
    public void testFindByApplicationIdOrderedReturnsEmptyForNonExistent() {
        List<TimelineEvent> result = dao.findByApplicationIdOrdered("NONEXIST");
        assertTrue("non-existent ID should return empty list", result.isEmpty());
    }

    // ---------- normaliseStage ----------

    /** normaliseStage() normalises the stage string. */
    public void testNormalizeStageNormalizesStageString() {
        assertEquals("empty string", "", TimelineDao.normalizeStage(null));
        assertEquals("to uppercase", "SUBMITTED", TimelineDao.normalizeStage("submitted"));
        assertEquals("trim whitespace and uppercase", "UNDER_REVIEW", TimelineDao.normalizeStage("  Under_Review  "));
        assertEquals("already standard format", "APPROVED", TimelineDao.normalizeStage("APPROVED"));
    }

    // ---------- Helper methods ----------

    private TimelineEvent createEvent(String eventId, String applicationId, String stage,
                                       String occurredAt, String note) {
        TimelineEvent event = new TimelineEvent();
        event.setEventId(eventId);
        event.setApplicationId(applicationId);
        event.setStage(stage);
        event.setOccurredAt(occurredAt);
        event.setNote(note);
        return event;
    }

    public static void main(String[] args) {
        new TimelineDaoTest().runTestsAndExit();
    }
}
