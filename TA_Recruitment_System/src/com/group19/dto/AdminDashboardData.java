package com.group19.dto;

import com.group19.model.Job;
import com.group19.model.UserAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin dashboard page data transfer object (DTO).
 * <p>
 * Encapsulates all data required by the admin dashboard page, including system user account lists,
 * job lists, various statistical counts (pending/accepted/rejected application counts,
 * workload warning count, time conflict count, etc.) and recent activity feed summaries.
 * </p>
 *
 * @author Group 19
 */
public class


AdminDashboardData {

    /** List of TA (Teaching Assistant) user accounts */
    private List<UserAccount> taAccounts = new ArrayList<>();

    /** List of MO (Module Organiser) user accounts */
    private List<UserAccount> moAccounts = new ArrayList<>();

    /** List of jobs */
    private List<Job> jobs = new ArrayList<>();

    /** The currently selected TA account */
    private UserAccount selectedTa;

    /** The currently selected MO account */
    private UserAccount selectedMo;

    /** The currently selected job */
    private Job selectedJob;

    /** Number of open jobs */
    private int openJobCount;

    /** Number of closed jobs */
    private int closedJobCount;

    /** Total number of TA users */
    private int taCount;

    /** Total number of MO users */
    private int moCount;

    /** Number of pending applications */
    private int pendingApplicationCount;

    /** Number of accepted applications */
    private int acceptedApplicationCount;

    /** Number of rejected applications */
    private int rejectedApplicationCount;

    /** Number of workload warnings */
    private int workloadWarningCount;

    /** Number of time conflicts */
    private int timeConflictCount;

    /** Recent job activity feed list */
    private List<AdminFeedItem> recentJobs = new ArrayList<>();

    /** Recent application activity feed list */
    private List<AdminFeedItem> recentApplications = new ArrayList<>();

    /** Recent alert activity feed list */
    private List<AdminFeedItem> recentAlerts = new ArrayList<>();

    /** @return list of TA user accounts */
    public List<UserAccount> getTaAccounts() {
        return taAccounts;
    }

    /** @param taAccounts list of TA user accounts */
    public void setTaAccounts(List<UserAccount> taAccounts) {
        this.taAccounts = taAccounts == null ? new ArrayList<>() : taAccounts;
    }

    /** @return list of MO user accounts */
    public List<UserAccount> getMoAccounts() {
        return moAccounts;
    }

    /** @param moAccounts list of MO user accounts */
    public void setMoAccounts(List<UserAccount> moAccounts) {
        this.moAccounts = moAccounts == null ? new ArrayList<>() : moAccounts;
    }

    /** @return list of jobs */
    public List<Job> getJobs() {
        return jobs;
    }

    /** @param jobs list of jobs */
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs == null ? new ArrayList<>() : jobs;
    }

    /** @return the currently selected TA account */
    public UserAccount getSelectedTa() {
        return selectedTa;
    }

    /** @param selectedTa the currently selected TA account */
    public void setSelectedTa(UserAccount selectedTa) {
        this.selectedTa = selectedTa;
    }

    /** @return the currently selected MO account */
    public UserAccount getSelectedMo() {
        return selectedMo;
    }

    /** @param selectedMo the currently selected MO account */
    public void setSelectedMo(UserAccount selectedMo) {
        this.selectedMo = selectedMo;
    }

    /** @return the currently selected job */
    public Job getSelectedJob() {
        return selectedJob;
    }

    /** @param selectedJob the currently selected job */
    public void setSelectedJob(Job selectedJob) {
        this.selectedJob = selectedJob;
    }

    /** @return number of open jobs */
    public int getOpenJobCount() {
        return openJobCount;
    }

    /** @param openJobCount number of open jobs */
    public void setOpenJobCount(int openJobCount) {
        this.openJobCount = openJobCount;
    }

    /** @return number of closed jobs */
    public int getClosedJobCount() {
        return closedJobCount;
    }

    /** @param closedJobCount number of closed jobs */
    public void setClosedJobCount(int closedJobCount) {
        this.closedJobCount = closedJobCount;
    }

    /** @return total number of TA users */
    public int getTaCount() {
        return taCount;
    }

    /** @param taCount total number of TA users */
    public void setTaCount(int taCount) {
        this.taCount = taCount;
    }

    /** @return total number of MO users */
    public int getMoCount() {
        return moCount;
    }

    /** @param moCount total number of MO users */
    public void setMoCount(int moCount) {
        this.moCount = moCount;
    }

    /** @return number of pending applications */
    public int getPendingApplicationCount() {
        return pendingApplicationCount;
    }

    /** @param pendingApplicationCount number of pending applications */
    public void setPendingApplicationCount(int pendingApplicationCount) {
        this.pendingApplicationCount = pendingApplicationCount;
    }

    /** @return number of accepted applications */
    public int getAcceptedApplicationCount() {
        return acceptedApplicationCount;
    }

    /** @param acceptedApplicationCount number of accepted applications */
    public void setAcceptedApplicationCount(int acceptedApplicationCount) {
        this.acceptedApplicationCount = acceptedApplicationCount;
    }

    /** @return number of rejected applications */
    public int getRejectedApplicationCount() {
        return rejectedApplicationCount;
    }

    /** @param rejectedApplicationCount number of rejected applications */
    public void setRejectedApplicationCount(int rejectedApplicationCount) {
        this.rejectedApplicationCount = rejectedApplicationCount;
    }

    /** @return number of workload warnings */
    public int getWorkloadWarningCount() {
        return workloadWarningCount;
    }

    /** @param workloadWarningCount number of workload warnings */
    public void setWorkloadWarningCount(int workloadWarningCount) {
        this.workloadWarningCount = workloadWarningCount;
    }

    /** @return number of time conflicts */
    public int getTimeConflictCount() {
        return timeConflictCount;
    }

    /** @param timeConflictCount number of time conflicts */
    public void setTimeConflictCount(int timeConflictCount) {
        this.timeConflictCount = timeConflictCount;
    }

    /** @return recent job activity feed list */
    public List<AdminFeedItem> getRecentJobs() {
        return recentJobs;
    }

    /** @param recentJobs recent job activity feed list */
    public void setRecentJobs(List<AdminFeedItem> recentJobs) {
        this.recentJobs = recentJobs == null ? new ArrayList<>() : recentJobs;
    }

    /** @return recent application activity feed list */
    public List<AdminFeedItem> getRecentApplications() {
        return recentApplications;
    }

    /** @param recentApplications recent application activity feed list */
    public void setRecentApplications(List<AdminFeedItem> recentApplications) {
        this.recentApplications = recentApplications == null ? new ArrayList<>() : recentApplications;
    }

    /** @return recent alert activity feed list */
    public List<AdminFeedItem> getRecentAlerts() {
        return recentAlerts;
    }

    /** @param recentAlerts recent alert activity feed list */
    public void setRecentAlerts(List<AdminFeedItem> recentAlerts) {
        this.recentAlerts = recentAlerts == null ? new ArrayList<>() : recentAlerts;
    }
}
