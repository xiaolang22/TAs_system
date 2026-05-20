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

public class SavedJobDao {
    private final Path savedJobFilePath;
    private final Type listType = new TypeToken<List<SavedJob>>() {
    }.getType();

    public SavedJobDao(Path savedJobFilePath) {
        this.savedJobFilePath = savedJobFilePath;
    }

    public List<SavedJob> findAll() {
        try {
            List<SavedJob> savedJobs = JsonFileUtil.readList(savedJobFilePath, listType);
            return savedJobs != null ? savedJobs : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
