package com.group19.dto;

import java.util.ArrayList;
import java.util.List;

public class TaWorkloadRow {
    private String studentId;
    private String name;
    private String email;
    private String programme;
    private List<AssignedPositionDto> assignedPositions = new ArrayList<>();
    private int assignedPositionCount;
    private double totalAssignedHours;
    private String totalAssignedHoursLabel;
    private List<String> workloadWarningReasons = new ArrayList<>();

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public List<AssignedPositionDto> getAssignedPositions() {
        return assignedPositions;
    }

    public void setAssignedPositions(List<AssignedPositionDto> assignedPositions) {
        this.assignedPositions = assignedPositions == null ? new ArrayList<>() : assignedPositions;
    }

    public int getAssignedPositionCount() {
        return assignedPositionCount;
    }

    public void setAssignedPositionCount(int assignedPositionCount) {
        this.assignedPositionCount = assignedPositionCount;
    }

    public double getTotalAssignedHours() {
        return totalAssignedHours;
    }

    public void setTotalAssignedHours(double totalAssignedHours) {
        this.totalAssignedHours = totalAssignedHours;
    }

    public String getTotalAssignedHoursLabel() {
        return totalAssignedHoursLabel;
    }

    public void setTotalAssignedHoursLabel(String totalAssignedHoursLabel) {
        this.totalAssignedHoursLabel = totalAssignedHoursLabel;
    }

    public List<String> getWorkloadWarningReasons() {
        return workloadWarningReasons;
    }

    public void setWorkloadWarningReasons(List<String> workloadWarningReasons) {
        this.workloadWarningReasons = workloadWarningReasons == null ? new ArrayList<>() : workloadWarningReasons;
    }

    public boolean isHasWorkloadWarning() {
        return workloadWarningReasons != null && !workloadWarningReasons.isEmpty();
    }
}
