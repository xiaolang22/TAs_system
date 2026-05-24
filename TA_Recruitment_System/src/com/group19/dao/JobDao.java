package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Job;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Job entities, responsible for reading and writing job data
 * stored in JSON files. Supports querying, creating, updating, and deleting job
 * records.
 *
 * @author Group19
 * @since 1.0
 */
public class JobDao {

    /** File path to the job data JSON file. */
    private final Path jobFilePath;

    /** Type token required for Gson deserialisation. */
    private final Type listType = new TypeToken<List<Job>>() {
    }.getType();

    /**
     * Constructs a new JobDao instance.
     *
     * @param jobFilePath file path to the job data JSON file
     */
    public JobDao(Path jobFilePath) {
        this.jobFilePath = jobFilePath;
    }

    /**
     * Retrieves all job records.
     *
     * @return a list of jobs, or an empty list if reading fails
     */
    public List<Job> findAll() {
        try {
            List<Job> jobs = JsonFileUtil.readList(jobFilePath, listType);
            return jobs != null ? jobs : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Finds a job by its ID.
     *
     * @param jobId the job ID
     * @return the matching Job, or {@code null} if not found
     */
    public Job findById(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return null;
        }
        for (Job job : findAll()) {
            if (jobId.equals(job.getJobId())) {
                return job;
            }
        }
        return null;
    }

    /**
     * Saves a new job record (appends to the JSON file).
     *
     * @param job the Job object to save
     * @return {@code true} if the write operation succeeds
     */
    public boolean save(Job job) {
        List<Job> jobs = findAll();
        jobs.add(job);
        try {
            JsonFileUtil.writeList(jobFilePath, jobs);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a job record by its ID (case-insensitive ID matching; replaces if
     * found).
     *
     * @param target the Job object containing updated data
     * @return {@code true} if found and updated successfully
     */
    public boolean update(Job target) {
        if (target == null || target.getJobId() == null || target.getJobId().isBlank()) {
            return false;
        }
        List<Job> jobs = findAll();
        for (int i = 0; i < jobs.size(); i++) {
            Job current = jobs.get(i);
            if (current != null && target.getJobId().equalsIgnoreCase(current.getJobId())) {
                jobs.set(i, target);
                try {
                    JsonFileUtil.writeList(jobFilePath, jobs);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Deletes a job record by its ID (case-insensitive).
     *
     * @param jobId the ID of the job to delete
     * @return {@code true} if deleted successfully; {@code false} if no matching job
     *         is found
     */
    public boolean delete(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return false;
        }
        List<Job> jobs = findAll();
        boolean removed = jobs.removeIf(job -> job != null && jobId.equalsIgnoreCase(job.getJobId()));
        if (!removed) {
            return false;
        }
        try {
            JsonFileUtil.writeList(jobFilePath, jobs);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
