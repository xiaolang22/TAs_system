package com.group19.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * TA workload row data transfer object (DTO).
 * <p>
 * Used for table row display on the admin workload monitoring page. Each record
 * corresponds to one TA and contains basic TA information, the list and count of
 * assigned positions, total assigned hours, a hours display label, and a list of
 * workload warning reasons.
 * </p>
 *
 * @author Group 19
 */
public class TaWorkloadRow {

    /** TA student ID */
    private String studentId;

    /** TA name */
    private String name;

    /** TA email */
    private String email;

    /** Programme of the TA */
    private String programme;

    /** List of assigned positions */
    private List<AssignedPositionDto> assignedPositions = new ArrayList<>();

    /** Number of assigned positions */
    private int assignedPositionCount;

    /** Total assigned hours (in hours) */
    private double totalAssignedHours;

    /** Display label for total assigned hours */
    private String totalAssignedHoursLabel;

    /** List of workload warning reasons (empty list indicates no warning) */
    private List<String> workloadWarningReasons = new ArrayList<>();

    /** @return TA student ID */
    public String getStudentId() {
        return studentId;
    }

    /** @param studentId TA student ID */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /** @return TA name */
    public String getName() {
        return name;
    }

    /** @param name TA name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return TA email */
    public String getEmail() {
        return email;
    }

    /** @param email TA email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return programme of the TA */
    public String getProgramme() {
        return programme;
    }

    /** @param programme programme of the TA */
    public void setProgramme(String programme) {
        this.programme = programme;
    }

    /** @return list of assigned positions */
    public List<AssignedPositionDto> getAssignedPositions() {
        return assignedPositions;
    }

    /** @param assignedPositions list of assigned positions */
    public void setAssignedPositions(List<AssignedPositionDto> assignedPositions) {
        this.assignedPositions = assignedPositions == null ? new ArrayList<>() : assignedPositions;
    }

    /** @return number of assigned positions */
    public int getAssignedPositionCount() {
        return assignedPositionCount;
    }

    /** @param assignedPositionCount number of assigned positions */
    public void setAssignedPositionCount(int assignedPositionCount) {
        this.assignedPositionCount = assignedPositionCount;
    }

    /** @return total assigned hours */
    public double getTotalAssignedHours() {
        return totalAssignedHours;
    }

    /** @param totalAssignedHours total assigned hours */
    public void setTotalAssignedHours(double totalAssignedHours) {
        this.totalAssignedHours = totalAssignedHours;
    }

    /** @return display label for total assigned hours */
    public String getTotalAssignedHoursLabel() {
        return totalAssignedHoursLabel;
    }

    /** @param totalAssignedHoursLabel display label for total assigned hours */
    public void setTotalAssignedHoursLabel(String totalAssignedHoursLabel) {
        this.totalAssignedHoursLabel = totalAssignedHoursLabel;
    }

    /** @return list of workload warning reasons */
    public List<String> getWorkloadWarningReasons() {
        return workloadWarningReasons;
    }

    /** @param workloadWarningReasons list of workload warning reasons */
    public void setWorkloadWarningReasons(List<String> workloadWarningReasons) {
        this.workloadWarningReasons = workloadWarningReasons == null ? new ArrayList<>() : workloadWarningReasons;
    }

    /**
     * Checks whether a workload warning exists.
     *
     * @return true if there are warning reasons present
     */
    public boolean isHasWorkloadWarning() {
        return workloadWarningReasons != null && !workloadWarningReasons.isEmpty();
    }
}
