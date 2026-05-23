package com.group19.model;

public class LoginUser {
    private final String username;
    private final String role;
    private final String displayName;
    private final String userId;
    private final String avatarPath;

    public LoginUser(String username, String role, String displayName, String userId) {
        this(username, role, displayName, userId, "");
    }

    public LoginUser(String username, String role, String displayName, String userId, String avatarPath) {
        this.username = username;
        this.role = role;
        this.displayName = displayName;
        this.userId = userId;
        this.avatarPath = avatarPath;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getAvatarPath() {
        return avatarPath;
    }
}
