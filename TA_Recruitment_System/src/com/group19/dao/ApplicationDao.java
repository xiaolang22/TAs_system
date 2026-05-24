package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Application;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Application entities, responsible for reading and writing
 * application data stored in JSON files. Data is persisted in JSON format and
 * serialised/deserialised via {@link JsonFileUtil}.
 *
 * @author Group19
 * @since 1.0
 */
public class ApplicationDao {

    /** File path to the application data JSON file. */
    private final Path applicationFilePath;

    /** Type token required for Gson deserialisation. */
    private final Type listType = new TypeToken<List<Application>>() {
    }.getType();

    /**
     * Constructs a new ApplicationDao instance.
     *
     * @param applicationFilePath file path to the application data JSON file
     */
    public ApplicationDao(Path applicationFilePath) {
        this.applicationFilePath = applicationFilePath;
    }

    /**
     * Retrieves all application records.
     *
     * @return a list of applications, or an empty list if reading fails
     */
    public List<Application> findAll() {
        try {
            List<Application> applications = JsonFileUtil.readList(applicationFilePath, listType);
            return applications != null ? applications : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Finds an application by its ID (case-insensitive).
     *
     * @param applicationId the application ID
     * @return the matching Application, or {@code null} if not found
     */
    public Application findByApplicationId(String applicationId) {
        if (applicationId == null || applicationId.isBlank()) {
            return null;
        }
        for (Application app : findAll()) {
            if (applicationId.equalsIgnoreCase(app.getApplicationId())) {
                return app;
            }
        }
        return null;
    }

    /**
     * Finds all applications associated with the given job ID (case-insensitive).
     *
     * @param jobId the job ID
     * @return a list of matching applications, or an empty list if the parameter is blank
     */
    public List<Application> findByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return new ArrayList<>();
        }
        List<Application> result = new ArrayList<>();
        for (Application app : findAll()) {
            if (jobId.equalsIgnoreCase(app.getJobId())) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Finds all applications associated with the given TA student ID (case-insensitive).
     *
     * @param taStudentId the TA student ID
     * @return a list of matching applications, or an empty list if the parameter is blank
     */
    public List<Application> findByTaStudentId(String taStudentId) {
        if (taStudentId == null || taStudentId.isBlank()) {
            return new ArrayList<>();
        }
        List<Application> result = new ArrayList<>();
        for (Application app : findAll()) {
            if (taStudentId.equalsIgnoreCase(app.getTaStudentId())) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Checks whether a student has already applied for a given job.
     *
     * @param jobId       the job ID
     * @param taStudentId the TA student ID
     * @return {@code true} if an application record already exists
     */
    public boolean hasApplied(String jobId, String taStudentId) {
        if (jobId == null || jobId.isBlank() || taStudentId == null || taStudentId.isBlank()) {
            return false;
        }
        for (Application app : findAll()) {
            if (jobId.equalsIgnoreCase(app.getJobId()) &&
                taStudentId.equalsIgnoreCase(app.getTaStudentId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves a new application record (appends to the JSON file).
     *
     * @param application the Application object to save
     * @return {@code true} if the write operation succeeds
     */
    public boolean save(Application application) {
        List<Application> applications = findAll();
        applications.add(application);
        try {
            JsonFileUtil.writeList(applicationFilePath, applications);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing application record identified by its ID (replaces if found;
     * no-op otherwise).
     *
     * @param application the Application object containing updated data
     * @return {@code true} if found and updated successfully; {@code false} if no
     *         matching record exists
     */
    public boolean update(Application application) {
        List<Application> applications = findAll();
        int index = -1;
        for (int i = 0; i < applications.size(); i++) {
            if (application.getApplicationId().equalsIgnoreCase(applications.get(i).getApplicationId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            applications.set(index, application);
            try {
                JsonFileUtil.writeList(applicationFilePath, applications);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
