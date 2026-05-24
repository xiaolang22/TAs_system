package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Notification;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Notification entities, responsible for reading and writing
 * notification data stored in JSON files. Supports querying notifications by
 * recipient user and batch mark-as-read operations.
 *
 * @author Group19
 * @since 1.0
 */
public class NotificationDao {

    /** File path to the notification data JSON file. */
    private final Path notificationFilePath;

    /** Type token required for Gson deserialisation. */
    private final Type listType = new TypeToken<List<Notification>>() {
    }.getType();

    /**
     * Constructs a new NotificationDao instance.
     *
     * @param notificationFilePath file path to the notification data JSON file
     */
    public NotificationDao(Path notificationFilePath) {
        this.notificationFilePath = notificationFilePath;
    }

    /**
     * Retrieves all notification records.
     *
     * @return a list of notifications, or an empty list if reading fails
     */
    public List<Notification> findAll() {
        try {
            List<Notification> notifications = JsonFileUtil.readList(notificationFilePath, listType);
            return notifications != null ? notifications : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves a new notification record (appends to the JSON file).
     *
     * @param notification the Notification object to save
     * @return {@code true} if the write operation succeeds
     */
    public boolean save(Notification notification) {
        List<Notification> notifications = findAll();
        notifications.add(notification);
        try {
            JsonFileUtil.writeList(notificationFilePath, notifications);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds all notifications for a given recipient user ID (case-insensitive).
     *
     * @param recipientUserId the recipient user ID
     * @return a list of matching notifications
     */
    public List<Notification> findByRecipientUserId(String recipientUserId) {
        if (recipientUserId == null || recipientUserId.isBlank()) {
            return new ArrayList<>();
        }
        List<Notification> result = new ArrayList<>();
        for (Notification notification : findAll()) {
            if (recipientUserId.equalsIgnoreCase(notification.getRecipientUserId())) {
                result.add(notification);
            }
        }
        return result;
    }

    /**
     * Marks notifications matching the given recipient, application, and type as
     * read.
     *
     * @param recipientUserId the recipient user ID
     * @param applicationId   the associated application ID
     * @param type            the notification type ({@code null} means match all types)
     * @return {@code true} if the operation succeeds
     */
    public boolean markAsReadByRecipientAndApplication(String recipientUserId, String applicationId, String type) {
        if (recipientUserId == null || recipientUserId.isBlank()
                || applicationId == null || applicationId.isBlank()) {
            return false;
        }

        List<Notification> notifications = findAll();
        boolean changed = false;
        for (Notification notification : notifications) {
            if (!recipientUserId.equalsIgnoreCase(notification.getRecipientUserId())) {
                continue;
            }
            if (type != null && !type.equals(notification.getType())) {
                continue;
            }
            if (!applicationId.equalsIgnoreCase(notification.getApplicationId())) {
                continue;
            }
            if (!notification.isRead()) {
                notification.setRead(true);
                changed = true;
            }
        }

        if (!changed) {
            return true;
        }

        try {
            JsonFileUtil.writeList(notificationFilePath, notifications);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Marks all notifications for a given recipient and type as read.
     *
     * @param recipientUserId the recipient user ID
     * @param type            the notification type ({@code null} means match all types)
     * @return {@code true} if the operation succeeds
     */
    public boolean markAllAsReadByRecipientAndType(String recipientUserId, String type) {
        if (recipientUserId == null || recipientUserId.isBlank()) {
            return false;
        }

        List<Notification> notifications = findAll();
        boolean changed = false;
        for (Notification notification : notifications) {
            if (!recipientUserId.equalsIgnoreCase(notification.getRecipientUserId())) {
                continue;
            }
            if (type != null && !type.equals(notification.getType())) {
                continue;
            }
            if (!notification.isRead()) {
                notification.setRead(true);
                changed = true;
            }
        }

        if (!changed) {
            return true;
        }

        try {
            JsonFileUtil.writeList(notificationFilePath, notifications);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
