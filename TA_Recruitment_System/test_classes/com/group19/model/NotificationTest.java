package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.Notification;

/**
 * Unit tests for the Notification model class.
 * Tests default constructor and all 8 getter/setter pairs including boolean isRead.
 *
 * @author Group19
 * @since 1.0
 */
public class NotificationTest extends TestRunner {

    public void testDefaultConstructorInitialisesAllFieldsToNullOrDefault() {
        Notification notif = new Notification();
        assertNotNull("Default constructor should create a non-null Notification object", notif);
        assertNull("notificationId should be null after default constructor", notif.getNotificationId());
        assertNull("recipientUserId should be null after default constructor", notif.getRecipientUserId());
        assertNull("type should be null after default constructor", notif.getType());
        assertNull("message should be null after default constructor", notif.getMessage());
        assertNull("applicationId should be null after default constructor", notif.getApplicationId());
        assertNull("jobId should be null after default constructor", notif.getJobId());
        assertFalse("isRead should be false after default constructor", notif.isRead());
        assertNull("createdAt should be null after default constructor", notif.getCreatedAt());
    }

    public void testSetAndGetNotificationId() {
        Notification notif = new Notification();
        notif.setNotificationId("NOTIF001");
        assertEquals("NOTIF001", notif.getNotificationId());
    }

    public void testSetAndGetRecipientUserId() {
        Notification notif = new Notification();
        notif.setRecipientUserId("STU001");
        assertEquals("STU001", notif.getRecipientUserId());
    }

    public void testSetAndGetType() {
        Notification notif = new Notification();
        notif.setType("status_update");
        assertEquals("status_update", notif.getType());
    }

    public void testSetAndGetMessage() {
        Notification notif = new Notification();
        notif.setMessage("Your application status has been updated");
        assertEquals("Your application status has been updated", notif.getMessage());
    }

    public void testSetAndGetApplicationId() {
        Notification notif = new Notification();
        notif.setApplicationId("APP001");
        assertEquals("APP001", notif.getApplicationId());
    }

    public void testSetAndGetJobId() {
        Notification notif = new Notification();
        notif.setJobId("JOB001");
        assertEquals("JOB001", notif.getJobId());
    }

    public void testSetAndIsRead() {
        Notification notif = new Notification();
        assertFalse("isRead should default to false", notif.isRead());
        notif.setRead(true);
        assertTrue("isRead should be true after setRead(true)", notif.isRead());
        notif.setRead(false);
        assertFalse("isRead should be false after setRead(false)", notif.isRead());
    }

    public void testSetAndGetCreatedAt() {
        Notification notif = new Notification();
        notif.setCreatedAt("2024-01-15T12:00:00");
        assertEquals("2024-01-15T12:00:00", notif.getCreatedAt());
    }

    public void testAllFieldsSetAndGetConsistently() {
        Notification notif = new Notification();
        notif.setNotificationId("NOTIF002");
        notif.setRecipientUserId("STU002");
        notif.setType("new_application");
        notif.setMessage("New application submitted");
        notif.setApplicationId("APP002");
        notif.setJobId("JOB002");
        notif.setRead(true);
        notif.setCreatedAt("2024-02-20T09:30:00");

        assertEquals("NOTIF002", notif.getNotificationId());
        assertEquals("STU002", notif.getRecipientUserId());
        assertEquals("new_application", notif.getType());
        assertEquals("New application submitted", notif.getMessage());
        assertEquals("APP002", notif.getApplicationId());
        assertEquals("JOB002", notif.getJobId());
        assertTrue("isRead should be true", notif.isRead());
        assertEquals("2024-02-20T09:30:00", notif.getCreatedAt());
    }

    public static void main(String[] args) {
        new NotificationTest().runTestsAndExit();
    }
}
