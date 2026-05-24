package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.SavedJob;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Data access object for SavedJob entities, responsible for reading and writing
 * saved (bookmarked) job data stored in JSON files. Supports querying saved jobs
 * by user, duplicate checking by user and job, and removing saved jobs.
 *
 * @author Group19
 * @since 1.0
 */
public class SavedJobDao {

    /** File path to the saved jobs data JSON file. */
    private final Path savedJobFilePath;

    /** Type token required for Gson deserialisation. */
    private final Type listType = new TypeToken<List<SavedJob>>() {
    }.getType();

    /**
     * Constructs a new SavedJobDao instance.
     *
     * @param savedJobFilePath file path to the saved jobs data JSON file
     */
    public SavedJobDao(Path savedJobFilePath) {
        this.savedJobFilePath = savedJobFilePath;
    }

    /**
     * Retrieves all saved job records.
     *
     * @return a list of saved jobs, or an empty list if reading fails
     */
    public List<SavedJob> findAll() {
        try {
            List<SavedJob> savedJobs = JsonFileUtil.readList(savedJobFilePath, listType);
            return savedJobs != null ? savedJobs : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Finds all saved job records for a given user ID (case-insensitive).
     *
     * @param userId the user ID
     * @return a list of saved jobs belonging to the user
     */
    public List<SavedJob> findByUserId(String userId) {
        List<SavedJob> result = new ArrayList<>();
        if (userId == null || userId.isBlank()) {
            return result;
        }

        String normalizedUserId = userId.trim();
        for (SavedJob savedJob : findAll()) {
            if (savedJob != null && normalizedUserId.equalsIgnoreCase(safe(savedJob.getUserId()))) {
                result.add(savedJob);
            }
        }
        return result;
    }

    /**
     * Finds a saved job record by user ID and job ID (case-insensitive).
     *
     * @param userId the user ID
     * @param jobId  the job ID
     * @return the matching SavedJob, or {@code null} if not found
     */
    public SavedJob findByUserIdAndJobId(String userId, String jobId) {
        if (userId == null || userId.isBlank() || jobId == null || jobId.isBlank()) {
            return null;
        }

        String normalizedUserId = userId.trim();
        String normalizedJobId = jobId.trim();
        for (SavedJob savedJob : findAll()) {
            if (savedJob != null
                    && normalizedUserId.equalsIgnoreCase(safe(savedJob.getUserId()))
                    && normalizedJobId.equalsIgnoreCase(safe(savedJob.getJobId()))) {
                return savedJob;
            }
        }
        return null;
    }

    /**
     * Saves a new saved job record (no-op if a duplicate already exists).
     *
     * @param savedJob the SavedJob object to save
     * @return {@code true} if the write operation succeeds
     */
    public boolean save(SavedJob savedJob) {
        if (savedJob == null || isBlank(savedJob.getUserId()) || isBlank(savedJob.getJobId())) {
            return false;
        }
        if (findByUserIdAndJobId(savedJob.getUserId(), savedJob.getJobId()) != null) {
            return true;
        }

        List<SavedJob> savedJobs = findAll();
        savedJobs.add(savedJob);
        try {
            JsonFileUtil.writeList(savedJobFilePath, savedJobs);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a saved job record by user ID and job ID.
     *
     * @param userId the user ID
     * @param jobId  the job ID
     * @return {@code true} if the deletion succeeds
     */
    public boolean delete(String userId, String jobId) {
        if (isBlank(userId) || isBlank(jobId)) {
            return false;
        }

        String normalizedUserId = userId.trim();
        String normalizedJobId = jobId.trim();
        List<SavedJob> savedJobs = findAll();
        boolean removed = false;
        Iterator<SavedJob> iterator = savedJobs.iterator();
        while (iterator.hasNext()) {
            SavedJob savedJob = iterator.next();
            if (savedJob != null
                    && normalizedUserId.equalsIgnoreCase(safe(savedJob.getUserId()))
                    && normalizedJobId.equalsIgnoreCase(safe(savedJob.getJobId()))) {
                iterator.remove();
                removed = true;
            }
        }

        if (!removed) {
            return true;
        }

        try {
            JsonFileUtil.writeList(savedJobFilePath, savedJobs);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Safely retrieves a string value, converting {@code null} to an empty string
     * and trimming whitespace.
     */
    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Checks whether a string is blank.
     */
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
