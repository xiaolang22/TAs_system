package com.group19.service;

import com.group19.dao.UserAccountDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.UserAccount;
import java.io.IOException;
import java.util.Locale;

public class AuthService {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private final UserAccountDao userAccountDao;

    public AuthService(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    public ServiceResult<LoginUser> login(String username, String password) {
        return login(null, username, password);
    }

    public ServiceResult<LoginUser> login(String role, String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return ServiceResult.failure("Please enter both username and password.");
        }

        String normalizedRole = normalizeRole(role);
        if (role != null && normalizedRole == null) {
            return ServiceResult.failure("Please choose a valid account type.");
        }

        try {
            UserAccount account = userAccountDao.findByUsername(username.trim());
            if (account == null || !password.trim().equals(account.getPassword())) {
                return ServiceResult.failure("Invalid username or password.");
            }
            if (account.isFrozen()) {
                return ServiceResult.failure("This account has been frozen.");
            }
            if (normalizedRole != null && !normalizedRole.equalsIgnoreCase(account.getRole())) {
                return ServiceResult.failure("Account type does not match.");
            }

            return ServiceResult.success(toLoginUser(account), "Login successful.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to load account data.");
        }
    }

    public ServiceResult<LoginUser> register(
            String role,
            String displayName,
            String userId,
            String username,
            String password,
            String confirmPassword) {
        String normalizedRole = normalizeUserRole(role);
        if (normalizedRole == null) {
            return ServiceResult.failure("Please choose TA or MO before registering.");
        }
        if (isBlank(displayName) || isBlank(userId) || isBlank(username) || isBlank(password) || isBlank(confirmPassword)) {
            return ServiceResult.failure("Please complete all registration fields.");
        }
        if (!password.trim().equals(confirmPassword.trim())) {
            return ServiceResult.failure("Passwords do not match.");
        }
        if (password.trim().length() < MIN_PASSWORD_LENGTH) {
            return ServiceResult.failure("Password must be at least 6 characters.");
        }

        String normalizedUsername = username.trim();
        String normalizedUserId = userId.trim();
        String normalizedDisplayName = displayName.trim();

        try {
            if (userAccountDao.findByUsername(normalizedUsername) != null) {
                return ServiceResult.failure("Username already exists.");
            }
            if (userAccountDao.findByUserId(normalizedUserId) != null) {
                return ServiceResult.failure("User ID already exists.");
            }

            UserAccount saved = userAccountDao.save(new UserAccount(
                    normalizedUsername,
                    password.trim(),
                    normalizedRole,
                    normalizedDisplayName,
                    normalizedUserId));

            return ServiceResult.success(toLoginUser(saved), "Registration successful.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save account data.");
        }
    }

    public UserAccount getPresetAccount(String role) {
        String normalizedRole = normalizeRole(role);
        if (normalizedRole == null) {
            return null;
        }

        try {
            return userAccountDao.findFirstByRole(normalizedRole);
        } catch (IOException e) {
            return null;
        }
    }

    public UserAccount getAccountByUserId(String userId) {
        if (isBlank(userId)) {
            return null;
        }
        try {
            return userAccountDao.findByUserId(userId.trim());
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static LoginUser toLoginUser(UserAccount account) {
        return new LoginUser(
                account.getUsername(),
                account.getRole(),
                account.getDisplayName(),
                account.getUserId(),
                account.getAvatarPath());
    }

    private static String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }

        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if ("TA".equals(normalized) || "MO".equals(normalized) || "ADMIN".equals(normalized)) {
            return normalized;
        }
        return null;
    }

    private static String normalizeUserRole(String role) {
        String normalized = normalizeRole(role);
        if ("TA".equals(normalized) || "MO".equals(normalized)) {
            return normalized;
        }
        return null;
    }
}
