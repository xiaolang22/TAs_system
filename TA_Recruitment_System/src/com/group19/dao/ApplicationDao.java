package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Application;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDao {
    private final Path applicationFilePath;
    private final Type listType = new TypeToken<List<Application>>() {
    }.getType();

    public ApplicationDao(Path applicationFilePath) {
        this.applicationFilePath = applicationFilePath;
    }

    public List<Application> findAll() {
        try {
            List<Application> applications = JsonFileUtil.readList(applicationFilePath, listType);
            return applications != null ? applications : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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
