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

/**
 * Data access object for TimelineEvent entities, responsible for reading and writing
 * timeline event data stored in JSON files. Supports querying events by application
 * ID with chronological ordering, and appending new events.
 *
 * @author Group19
 * @since 1.0
 */
public class TimelineDao {

    /** File path to the timeline data JSON file. */
    private final Path timelineFilePath;

    /** Type token required for Gson deserialisation. */
    private final Type listType = new TypeToken<List<TimelineEvent>>() {
    }.getType();

    /**
     * Constructs a new TimelineDao instance.
     *
     * @param timelineFilePath file path to the timeline data JSON file
     */
    public TimelineDao(Path timelineFilePath) {
        this.timelineFilePath = timelineFilePath;
    }

    /**
     * Retrieves all timeline events.
     *
     * @return a list of events, or an empty list if reading fails
     */
    public List<TimelineEvent> findAll() {
        try {
            List<TimelineEvent> events = JsonFileUtil.readList(timelineFilePath, listType);
            return events != null ? events : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Finds all timeline events associated with an application ID, ordered
     * chronologically by occurrence time.
     *
     * @param applicationId the application ID
     * @return a chronologically ordered list of events
     */
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

    /**
     * Appends a new timeline event (persists to the JSON file).
     *
     * @param event the TimelineEvent object to append
     * @return {@code true} if the append operation succeeds
     */
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

    /**
     * Safely retrieves a time string, returning an empty string for {@code null} or
     * blank values.
     */
    private static String safeTime(String occurredAt) {
        if (occurredAt == null || occurredAt.isBlank()) {
            return "";
        }
        return occurredAt.trim();
    }

    /**
     * Normalises a workflow stage name by trimming whitespace and converting to
     * uppercase.
     *
     * @param stage the raw stage name
     * @return the normalised stage name
     */
    public static String normalizeStage(String stage) {
        if (stage == null) {
            return "";
        }
        return stage.trim().toUpperCase(Locale.ROOT);
    }
}
