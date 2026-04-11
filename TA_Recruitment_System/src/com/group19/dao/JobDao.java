package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Job;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JobDao {
    private final Path jobFilePath;
    private final Type listType = new TypeToken<List<Job>>() {
    }.getType();

    public JobDao() {
        this(Paths.get(System.getProperty("user.dir"), "data", "jobs.json"));
    }

    public JobDao(Path jobFilePath) {
        this.jobFilePath = jobFilePath;
    }

    public List<Job> findAll() {
        try {
            if (jobFilePath == null || !Files.exists(jobFilePath)) {
                return new ArrayList<>();
            }
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
            if (jobFilePath == null) {
                return false;
            }
            JsonFileUtil.writeList(jobFilePath, jobs);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}