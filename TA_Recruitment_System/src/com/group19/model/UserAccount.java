package com.group19.model;

public class UserAccount {
    private String username;
    private String password;
    private String role;
    private String displayName;
    private String userId;
    private String avatarPath;
    private boolean frozen;

    public UserAccount() {
    }

    public UserAccount(
            String username,
            String password,
            String role,
            String displayName,
            String userId) {
        this(username, password, role, displayName, userId, "", false);
    }

    public UserAccount(
            String username,
            String password,
            String role,
            String displayName,
            String userId,
            String avatarPath) {
        this(username, password, role, displayName, userId, avatarPath, false);
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}
