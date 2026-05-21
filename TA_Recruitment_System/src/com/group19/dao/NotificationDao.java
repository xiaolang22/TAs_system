package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Notification;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NotificationDao {
    private final Path notificationFilePath;
    private final Type listType = new TypeToken<List<Notification>>() {
    }.getType();

    public NotificationDao(Path notificationFilePath) {
        this.notificationFilePath = notificationFilePath;
    }

    public List<Notification> findAll() {
        try {
            List<Notification> notifications = JsonFileUtil.readList(notificationFilePath, listType);
            return notifications != null ? notifications : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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
}
