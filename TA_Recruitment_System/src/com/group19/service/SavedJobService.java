package com.group19.service;

import com.group19.dao.JobDao;
import com.group19.dao.SavedJobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;
import com.group19.model.SavedJob;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SavedJobService {
    private final SavedJobDao savedJobDao;
    private final JobDao jobDao;

    public SavedJobService(SavedJobDao savedJobDao, JobDao jobDao) {
        this.savedJobDao = savedJobDao;
        this.jobDao = jobDao;
    }

    public boolean isSaved(String userId, String jobId) {
        return savedJobDao.findByUserIdAndJobId(userId, jobId) != null;
    }

    public Set<String> findSavedJobIds(String userId) {
        Set<String> ids = new LinkedHashSet<>();
        for (SavedJob savedJob : savedJobDao.findByUserId(userId)) {
            if (savedJob.getJobId() != null && !savedJob.getJobId().isBlank()) {
                ids.add(savedJob.getJobId().trim());
            }
        }
        return ids;
    }

    public List<Job> findSavedJobs(String userId) {
        List<SavedJob> savedRecords = savedJobDao.findByUserId(userId);
        savedRecords.sort(Comparator.comparing(SavedJobService::safeSavedAt).reversed());

        List<Job> jobs = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (SavedJob savedRecord : savedRecords) {
            String jobId = savedRecord.getJobId();
            if (jobId == null || jobId.isBlank() || !seen.add(jobId.trim().toLowerCase())) {
                continue;
            }
            Job job = jobDao.findById(jobId.trim());
            if (job != null) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    public ServiceResult<Void> saveJob(String userId, String jobId) {
        if (isBlank(userId)) {
            return ServiceResult.failure("请先登录后再收藏职位。");
        }
        if (isBlank(jobId)) {
            return ServiceResult.failure("缺少岗位编号。");
        }
        if (jobDao.findById(jobId.trim()) == null) {
            return ServiceResult.failure("未找到对应岗位。");
        }

        SavedJob savedJob = new SavedJob(userId.trim(), jobId.trim(), LocalDateTime.now().toString());
        boolean saved = savedJobDao.save(savedJob);
        if (!saved) {
            return ServiceResult.failure("收藏职位失败。");
        }
        return ServiceResult.success(null, "职位已收藏。");
    }

    public ServiceResult<Void> removeSavedJob(String userId, String jobId) {
        if (isBlank(userId)) {
            return ServiceResult.failure("请先登录后再取消收藏。");
        }
        if (isBlank(jobId)) {
            return ServiceResult.failure("缺少岗位编号。");
        }

        boolean removed = savedJobDao.delete(userId.trim(), jobId.trim());
        if (!removed) {
            return ServiceResult.failure("取消收藏失败。");
        }
        return ServiceResult.success(null, "已取消收藏。");
    }

    private static String safeSavedAt(SavedJob savedJob) {
        return savedJob == null || savedJob.getSavedAt() == null ? "" : savedJob.getSavedAt();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
