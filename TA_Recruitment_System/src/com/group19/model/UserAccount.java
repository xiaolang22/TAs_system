package com.group19.model;

/**
 * User account entity class, recording login account information for all users
 * in the system. Supports three roles: TA, MO, and ADMIN; contains frozen status
 * management and avatar path.
 *
 * @author Group19
 * @since 1.0
 */
public class UserAccount {

    /** Username */
    private String username;

    /** Login password */
    private String password;

    /** User role (TA / MO / ADMIN) */
    private String role;

    /** Display name */
    private String displayName;

    /** Unique identifier for the user (student/staff ID) */
    private String userId;

    /** Avatar file path */
    private String avatarPath;

    /** Whether the account is frozen */
    private boolean frozen;

    /**
     * Default no-argument constructor.
     */
    public UserAccount() {
    }

    /**
     * Creates a basic user account (not frozen by default, no avatar).
     *
     * @param username    Username
     * @param password    Password
     * @param role        Role
     * @param displayName Display name
     * @param userId      Unique identifier for the user
     */
    public UserAccount(
            String username,
            String password,
            String role,
            String displayName,
            String userId) {
        this(username, password, role, displayName, userId, "", false);
    }

    /**
     * Creates a user account with an avatar (not frozen by default).
     *
     * @param username    Username
     * @param password    Password
     * @param role        Role
     * @param displayName Display name
     * @param userId      Unique identifier for the user
     * @param avatarPath  Avatar path
     */
    public UserAccount(
            String username,
            String password,
            String role,
            String displayName,
            String userId,
            String avatarPath) {
        this(username, password, role, displayName, userId, avatarPath, false);
    }

    /**
     * Creates a complete user account.
     *
     * @param username    Username
     * @param password    Password
     * @param role        Role
     * @param displayName Display name
     * @param userId      Unique identifier for the user
     * @param avatarPath  Avatar path
     * @param frozen      Whether the account is frozen
     */
    public UserAccount(
            String username,
            String password,
            String role,
            String displayName,
            String userId,
            String avatarPath,
            boolean frozen) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
        this.userId = userId;
        this.avatarPath = avatarPath;
        this.frozen = frozen;
    }

    /** @return Username */
    public String getUsername() { return username; }
    /** @param username Username */
    public void setUsername(String username) { this.username = username; }

    /** @return Password */
    public String getPassword() { return password; }
    /** @param password Password */
    public void setPassword(String password) { this.password = password; }

    /** @return Role */
    public String getRole() { return role; }
    /** @param role Role */
    public void setRole(String role) { this.role = role; }

    /** @return Display name */
    public String getDisplayName() { return displayName; }
    /** @param displayName Display name */
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    /** @return Unique identifier for the user */
    public String getUserId() { return userId; }
    /** @param userId Unique identifier for the user */
    public void setUserId(String userId) { this.userId = userId; }

    /** @return Avatar path */
    public String getAvatarPath() { return avatarPath; }
    /** @param avatarPath Avatar path */
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    /** @return Whether the account is frozen */
    public boolean isFrozen() { return frozen; }
    /** @param frozen Whether the account is frozen */
    public void setFrozen(boolean frozen) { this.frozen = frozen; }
}
