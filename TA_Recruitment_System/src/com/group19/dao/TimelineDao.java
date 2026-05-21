package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.TimelineEvent;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TimelineDao {
    private final Path timelineFilePath;
    private final Type listType = new TypeToken<List<TimelineEvent>>() {
    }.getType();

    public TimelineDao(Path timelineFilePath) {
        this.timelineFilePath = timelineFilePath;
    }

    public List<TimelineEvent> findAll() {
        try {
            List<TimelineEvent> events = JsonFileUtil.readList(timelineFilePath, listType);
            return events != null ? events : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<TimelineEvent> findByApplicationIdOrdered(String applicationId) {
        if (applicationId == null || applicationId.isBlank()) {
            return new ArrayList<>();
        }
        String key = applicationId.trim();
        List<TimelineEvent> matches = new ArrayList<>();
        for (TimelineEvent event : findAll()) {
            if (event.getApplicationId() != null && key.equalsIgnoreCase(event.getApplicationId().trim())) {
                matches.add(event);
            }
        }
        matches.sort(Comparator.comparing(e -> safeTime(e.getOccurredAt())));
        return matches;
    }

    public boolean append(TimelineEvent event) {
        if (event == null) {
            return false;
        }
        List<TimelineEvent> events = findAll();
        events.add(event);
        try {
            JsonFileUtil.writeList(timelineFilePath, events);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String safeTime(String occurredAt) {
        if (occurredAt == null || occurredAt.isBlank()) {
            return "";
        }
        return occurredAt.trim();
    }

    public static String normalizeStage(String stage) {
        if (stage == null) {
            return "";
        }
        return stage.trim().toUpperCase(Locale.ROOT);
    }
}
