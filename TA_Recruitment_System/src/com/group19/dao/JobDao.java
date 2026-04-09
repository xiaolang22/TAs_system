package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.Job;
import com.group19.util.JsonFileUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JobDao {

    private static final Path FILE_PATH = Paths.get("data", "jobs.json");

    public List<Job> findAll() {
        Type listType = new TypeToken<List<Job>>() {}.getType();
        try {
            List<Job> jobs = JsonFileUtil.readList(FILE_PATH, listType);
            return jobs != null ? jobs : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean save(Job job) {
        List<Job> jobs = findAll();
        jobs.add(job);
        try {
            JsonFileUtil.writeList(FILE_PATH, jobs);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}