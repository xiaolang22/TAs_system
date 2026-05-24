package com.group19.model;

/**
 * Login user information entity class (immutable), used to store basic information
 * of the currently logged-in user in the session.
 * Converted from {@link UserAccount}, containing only display-related fields
 * without sensitive information such as the password.
 *
 * @author Group19
 * @since 1.0
 */
public class LoginUser {

    /** Username */
    private final String username;

    /** User role (TA / MO / ADMIN) */
    private final String role;

    /** Display name */
    private final String displayName;

    /** Unique identifier for the user (student/staff ID) */
    private final String userId;

    /** Avatar file path */
    private final String avatarPath;

    /**
     * Creates a LoginUser with basic information (no avatar).
     *
     * @param username    Username
     * @param role        Role
     * @param displayName Display name
     * @param userId      Unique identifier for the user
     */
    public LoginUser(String username, String role, String displayName, String userId) {
        this(username, role, displayName, userId, "");
    }

    /**
     * Creates a LoginUser with complete information.
     *
     * @param username    Username
     * @param role        Role
     * @param displayName Display name
     * @param userId      Unique identifier for the user
     * @param avatarPath  Avatar path
     */
    public LoginUser(String username, String role, String displayName, String userId, String avatarPath) {
        this.username = username;
        this.role = role;
        this.displayName = displayName;
        this.userId = userId;
        this.avatarPath = avatarPath;
    }

    /** @return Username */
    public String getUsername() { return username; }

    /** @return User role */
    public String getRole() { return role; }

    /** @return Display name */
    public String getDisplayName() { return displayName; }

    /** @return Unique identifier for the user */
    public String getUserId() { return userId; }

    /** @return Avatar file path */
    public String getAvatarPath() { return avatarPath; }
}
