package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Job;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JobDao {
    private final Path jobFilePath;
    private final Type listType = new TypeToken<List<Job>>() {
    }.getType();

    public JobDao(Path jobFilePath) {
        this.jobFilePath = jobFilePath;
    }

    public List<Job> findAll() {
        try {
            List<Job> jobs = JsonFileUtil.readList(jobFilePath, listType);
            return jobs != null ? jobs : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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
