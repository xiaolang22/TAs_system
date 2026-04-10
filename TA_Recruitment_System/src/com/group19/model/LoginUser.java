package com.group19.model;

public class LoginUser {
    private final String username;
    private final String role;
    private final String displayName;
    private final String userId;

    public LoginUser(String username, String role, String displayName, String userId) {
        this.username = username;
        this.role = role;
        this.displayName = displayName;
        this.userId = userId;
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
}
