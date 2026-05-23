package com.group19.dto;

import com.group19.model.Job;
import com.group19.model.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardData {
    private List<UserAccount> taAccounts = new ArrayList<>();
    private List<UserAccount> moAccounts = new ArrayList<>();
    private List<Job> jobs = new ArrayList<>();
    private UserAccount selectedTa;
    private UserAccount selectedMo;
    private Job selectedJob;
    private int openJobCount;
    private int closedJobCount;
    private int taCount;
    private int moCount;
    private int pendingApplicationCount;
    private int acceptedApplicationCount;
    private int rejectedApplicationCount;
    private int workloadWarningCount;
    private int timeConflictCount;
    private List<AdminFeedItem> recentJobs = new ArrayList<>();
    private List<AdminFeedItem> recentApplications = new ArrayList<>();
    private List<AdminFeedItem> recentAlerts = new ArrayList<>();

    public List<UserAccount> getTaAccounts() {
        return taAccounts;
    }

    public void setTaAccounts(List<UserAccount> taAccounts) {
        this.taAccounts = taAccounts == null ? new ArrayList<>() : taAccounts;
    }

    public List<UserAccount> getMoAccounts() {
        return moAccounts;
    }

    public void setMoAccounts(List<UserAccount> moAccounts) {
        this.moAccounts = moAccounts == null ? new ArrayList<>() : moAccounts;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs == null ? new ArrayList<>() : jobs;
    }

    public UserAccount getSelectedTa() {
        return selectedTa;
    }

    public void setSelectedTa(UserAccount selectedTa) {
        this.selectedTa = selectedTa;
    }

    public UserAccount getSelectedMo() {
        return selectedMo;
    }

    public void setSelectedMo(UserAccount selectedMo) {
        this.selectedMo = selectedMo;
    }

    public Job getSelectedJob() {
        return selectedJob;
    }

    public void setSelectedJob(Job selectedJob) {
        this.selectedJob = selectedJob;
    }

    public int getOpenJobCount() {
        return openJobCount;
    }

    public void setOpenJobCount(int openJobCount) {
        this.openJobCount = openJobCount;
    }

    public int getClosedJobCount() {
        return closedJobCount;
    }

    public void setClosedJobCount(int closedJobCount) {
        this.closedJobCount = closedJobCount;
    }

    public int getTaCount() {
        return taCount;
    }

    public void setTaCount(int taCount) {
        this.taCount = taCount;
    }

    public int getMoCount() {
        return moCount;
    }

    public void setMoCount(int moCount) {
        this.moCount = moCount;
    }

    public int getPendingApplicationCount() {
        return pendingApplicationCount;
    }

    public void setPendingApplicationCount(int pendingApplicationCount) {
        this.pendingApplicationCount = pendingApplicationCount;
    }

    public int getAcceptedApplicationCount() {
        return acceptedApplicationCount;
    }

    public void setAcceptedApplicationCount(int acceptedApplicationCount) {
        this.acceptedApplicationCount = acceptedApplicationCount;
    }

    public int getRejectedApplicationCount() {
        return rejectedApplicationCount;
    }

    public void setRejectedApplicationCount(int rejectedApplicationCount) {
        this.rejectedApplicationCount = rejectedApplicationCount;
    }

    public int getWorkloadWarningCount() {
        return workloadWarningCount;
    }

    public void setWorkloadWarningCount(int workloadWarningCount) {
        this.workloadWarningCount = workloadWarningCount;
    }

    public int getTimeConflictCount() {
        return timeConflictCount;
    }

    public void setTimeConflictCount(int timeConflictCount) {
        this.timeConflictCount = timeConflictCount;
    }

    public List<AdminFeedItem> getRecentJobs() {
        return recentJobs;
    }

    public void setRecentJobs(List<AdminFeedItem> recentJobs) {
        this.recentJobs = recentJobs == null ? new ArrayList<>() : recentJobs;
    }

    public List<AdminFeedItem> getRecentApplications() {
        return recentApplications;
    }

    public void setRecentApplications(List<AdminFeedItem> recentApplications) {
        this.recentApplications = recentApplications == null ? new ArrayList<>() : recentApplications;
    }

    public List<AdminFeedItem> getRecentAlerts() {
        return recentAlerts;
    }

    public void setRecentAlerts(List<AdminFeedItem> recentAlerts) {
        this.recentAlerts = recentAlerts == null ? new ArrayList<>() : recentAlerts;
    }
}
