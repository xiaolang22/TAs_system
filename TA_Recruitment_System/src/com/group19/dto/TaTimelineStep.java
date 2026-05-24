package com.group19.dto;

/**
 * TA application timeline step data transfer object (DTO).
 * <p>
 * Used for the timeline display on the TA application detail page. Each step
 * represents a key node in the application process (e.g., application submitted,
 * under review, accepted, etc.). Contains the step title, occurrence time, a
 * detailed description, and a status style class.
 * </p>
 *
 * @author Group 19
 */
public class TaTimelineStep {

    /** Timeline step title (e.g., "Application Submitted") */
    private String title;

    /** Display text for the step occurrence time */
    private String occurredAtDisplay;

    /** Detailed description of the step */
    private String detail;

    /** CSS style class for the step status */
    private String pillClass;

    /** @return timeline step title */
    public String getTitle() {
        return title;
    }

    /** @param title timeline step title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return display text for the step occurrence time */
    public String getOccurredAtDisplay() {
        return occurredAtDisplay;
    }

    /** @param occurredAtDisplay display text for the step occurrence time */
    public void setOccurredAtDisplay(String occurredAtDisplay) {
        this.occurredAtDisplay = occurredAtDisplay;
    }

    /** @return detailed description of the step */
    public String getDetail() {
        return detail;
    }

    /** @param detail detailed description of the step */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /** @return CSS style class */
    public String getPillClass() {
        return pillClass;
    }

    /** @param pillClass CSS style class */
    public void setPillClass(String pillClass) {
        this.pillClass = pillClass;
    }
}
