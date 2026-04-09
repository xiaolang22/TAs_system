package com.group19.model;

public class UserAccount {
    private String username;
    private String password;
    private String role;
    private String displayName;
    private String userId;

    public UserAccount() {
    }

    public UserAccount(
            String username,
            String password,
            String role,
            String displayName,
            String userId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
        this.userId = userId;
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
}
