package com.group19.dao;

import com.group19.TestRunner;
import com.group19.model.Notification;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for NotificationDao. Uses temporary JSON files for data read/write verification.
 *
 * @author Group19
 * @since 1.0
 */
public class NotificationDaoTest extends TestRunner {

    private Path tempFile;
    private NotificationDao dao;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("test-notifications-", ".json");
        Files.writeString(tempFile, "[]");
        dao = new NotificationDao(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    // ---------- findAll ----------

    /** findAll() returns an empty list initially. */
    public void testFindAllReturnsEmptyListInitially() {
        List<Notification> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertTrue("initial list should be empty", result.isEmpty());
    }

    // ---------- save ----------

    /** save() adds a new notification. */
    public void testSaveAddsNotification() {
        Notification notif = createNotification("NOTIF-001", "USER-001", "status_update",
                "Your application status has been updated", "APP-001", false);
        boolean saved = dao.save(notif);
        assertTrue("save() should return true", saved);

        List<Notification> all = dao.findAll();
        assertEquals("one record should exist after saving", 1, all.size());
        assertEquals("NOTIF-001", all.get(0).getNotificationId());
    }

    // ---------- findByRecipientUserId ----------

    /** findByRecipientUserId() returns matching notifications. */
    public void testFindByRecipientUserIdReturnsMatchingNotifications() {
        dao.save(createNotification("N1", "USER-001", "status_update", "Message 1", "APP-001", false));
        dao.save(createNotification("N2", "USER-002", "new_application", "Message 2", "APP-002", false));
        dao.save(createNotification("N3", "USER-001", "decision", "Message 3", "APP-003", false));

        List<Notification> result = dao.findByRecipientUserId("USER-001");
        assertEquals("USER-001 should have two notifications", 2, result.size());

        List<Notification> noMatch = dao.findByRecipientUserId("USER-999");
        assertTrue("non-existent user should return empty list", noMatch.isEmpty());
    }

    // ---------- markAsReadByRecipientAndApplication ----------

    /** markAsReadByRecipientAndApplication() marks matching notifications as read. */
    public void testMarkAsReadByRecipientAndApplicationMarksMatchingAsRead() {
        Notification n1 = createNotification("N1", "USER-001", "status_update", "Message 1", "APP-001", false);
        Notification n2 = createNotification("N2", "USER-001", "decision", "Message 2", "APP-001", false);
        Notification n3 = createNotification("N3", "USER-001", "status_update", "Message 3", "APP-002", false);
        dao.save(n1);
        dao.save(n2);
        dao.save(n3);

        // Mark USER-001 + APP-001 + status_update
        boolean result = dao.markAsReadByRecipientAndApplication("USER-001", "APP-001", "status_update");
        assertTrue("mark operation should return true", result);

        List<Notification> all = dao.findAll();
        for (Notification n : all) {
            if ("N1".equals(n.getNotificationId())) {
                assertTrue("N1 should be marked as read", n.isRead());
            }
            if ("N2".equals(n.getNotificationId())) {
                // type does not match, should not be marked
                assertFalse("N2 type does not match, should not be marked", n.isRead());
            }
            if ("N3".equals(n.getNotificationId())) {
                // applicationId does not match, should not be marked
                assertFalse("N3 applicationId does not match, should not be marked", n.isRead());
            }
        }
    }

    // ---------- markAllAsReadByRecipientAndType ----------

    /** markAllAsReadByRecipientAndType() marks all matching notifications as read. */
    public void testMarkAllAsReadByRecipientAndTypeMarksAllMatchingAsRead() {
        dao.save(createNotification("N1", "USER-001", "status_update", "Message 1", "APP-001", false));
        dao.save(createNotification("N2", "USER-001", "status_update", "Message 2", "APP-002", false));
        dao.save(createNotification("N3", "USER-001", "decision", "Message 3", "APP-003", false));
        dao.save(createNotification("N4", "USER-002", "status_update", "Message 4", "APP-004", false));

        // Mark USER-001 + status_update
        boolean result = dao.markAllAsReadByRecipientAndType("USER-001", "status_update");
        assertTrue("mark operation should return true", result);

        List<Notification> all = dao.findAll();
        for (Notification n : all) {
            if ("N1".equals(n.getNotificationId()) || "N2".equals(n.getNotificationId())) {
                assertTrue(n.getNotificationId() + " should be marked as read", n.isRead());
            }
            if ("N3".equals(n.getNotificationId())) {
                assertFalse("N3 type does not match, should not be marked", n.isRead());
            }
            if ("N4".equals(n.getNotificationId())) {
                assertFalse("N4 recipient does not match, should not be marked", n.isRead());
            }
        }
    }

    /** markAllAsReadByRecipientAndType() with null type matches all types. */
    public void testMarkAllAsReadWithNullTypeMatchesAll() {
        dao.save(createNotification("N1", "USER-001", "status_update", "Message 1", "APP-001", false));
        dao.save(createNotification("N2", "USER-001", "decision", "Message 2", "APP-002", false));
        dao.save(createNotification("N3", "USER-002", "status_update", "Message 3", "APP-003", false));

        // type=null should match all types
        boolean result = dao.markAllAsReadByRecipientAndType("USER-001", null);
        assertTrue("mark operation should return true", result);

        List<Notification> all = dao.findAll();
        for (Notification n : all) {
            if ("USER-001".equals(n.getRecipientUserId())) {
                assertTrue(n.getNotificationId() + " should be marked as read", n.isRead());
            }
            if ("USER-002".equals(n.getRecipientUserId())) {
                assertFalse("USER-002's notifications should not be marked", n.isRead());
            }
        }
    }

    // ---------- Helper methods ----------

    private Notification createNotification(String id, String recipientUserId, String type,
                                             String message, String applicationId, boolean read) {
        Notification n = new Notification();
        n.setNotificationId(id);
        n.setRecipientUserId(recipientUserId);
        n.setType(type);
        n.setMessage(message);
        n.setApplicationId(applicationId);
        n.setRead(read);
        n.setCreatedAt("2025-01-01 10:00:00");
        return n;
    }

    public static void main(String[] args) {
        new NotificationDaoTest().runTestsAndExit();
    }
}
