package com.group19.dto;

/**
 * Admin dashboard activity feed item data transfer object (DTO).
 * <p>
 * Used for the "Recent Activity" area of the admin dashboard. Each feed item contains
 * a title and a short meta-information description. It may represent different types of
 * activity such as recent job changes, application progress, or system alerts.
 * </p>
 *
 * @author Group 19
 */
public class AdminFeedItem {

    /** Title of the activity feed item */
    private String title;

    /** Meta-information of the activity feed item (e.g., time, type, associated object) */
    private String meta;

    /**
     * Default no-argument constructor.
     */
    public AdminFeedItem() {
    }

    /**
     * Parameterised constructor for creating an activity feed item with a title and meta-information.
     *
     * @param title title of the activity feed item
     * @param meta  meta-information of the activity feed item
     */
    public AdminFeedItem(String title, String meta) {
        this.title = title;
        this.meta = meta;
    }

    /**
     * @return title of the activity feed item
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title title of the activity feed item
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return meta-information of the activity feed item
     */
    public String getMeta() {
        return meta;
    }

    /**
     * @param meta meta-information of the activity feed item
     */
    public void setMeta(String meta) {
        this.meta = meta;
    }
}
