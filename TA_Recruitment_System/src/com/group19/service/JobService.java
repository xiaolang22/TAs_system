package com.group19.service;

import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

public class JobService {

    private final JobDao jobDao;

    public JobService(JobDao jobDao) {
        this.jobDao = jobDao;
    }

    public ServiceResult<Job> createJob(Job job) {
        if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
            return ServiceResult.failure("Title is required");
        }
        if (job.getCategory() == null || job.getCategory().trim().isEmpty()) {
            return ServiceResult.failure("Category is required");
        }
        if (job.getDescription() == null || job.getDescription().trim().isEmpty()) {
            return ServiceResult.failure("Description is required");
        }
        if (job.getRequirements() == null || job.getRequirements().trim().isEmpty()) {
            return ServiceResult.failure("Requirements are required");
        }
        if (job.getHours() == null || job.getHours().trim().isEmpty()) {
            return ServiceResult.failure("Hours are required");
        }
        if (job.getSchedule() == null || job.getSchedule().trim().isEmpty()) {
            return ServiceResult.failure("Schedule is required");
        }
        if (job.getDeadline() == null || job.getDeadline().trim().isEmpty()) {
            return ServiceResult.failure("Deadline is required");
        }

        job.setJobId(UUID.randomUUID().toString());
        job.setStatus("OPEN");
        job.setCreatedAt(LocalDateTime.now().toString());

        boolean success = jobDao.save(job);
        if (!success) {
            return ServiceResult.failure("Failed to save job");
        }

        return ServiceResult.success(job, "Job posted successfully");
    }
}