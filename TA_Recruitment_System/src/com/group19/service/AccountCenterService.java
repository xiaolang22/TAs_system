package com.group19.service;

import com.group19.dao.UserAccountDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.UserAccount;
import com.group19.util.FileUploadUtil;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Account centre service handling user profile, password, and avatar management.
 * Supports updating username/display name, changing password, and uploading avatars.
 *
 * @author Group19
 * @since 1.0
 */
public class AccountCenterService {
    private final UserAccountDao userAccountDao;

    public AccountCenterService(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    public ServiceResult<UserAccount> loadAccount(String userId) {
        if (isBlank(userId)) {
            return ServiceResult.failure("Unable to load account information.");
        }
        try {
            UserAccount account = userAccountDao.findByUserId(userId.trim());
            if (account == null) {
                return ServiceResult.failure("The current account could not be found.");
            }
            return ServiceResult.success(account, "Account information loaded successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to read account information.");
        }
    }

    public ServiceResult<LoginUser> updateProfile(String currentUserId, String displayName, String username) {
        if (isBlank(currentUserId)) {
            return ServiceResult.failure("The current login session is invalid. Please sign in again.");
        }
        if (isBlank(displayName) || isBlank(username)) {
            return ServiceResult.failure("Name and username are required.");
        }

        try {
            UserAccount existing = userAccountDao.findByUserId(currentUserId.trim());
            if (existing == null) {
                return ServiceResult.failure("The account to update could not be found.");
            }

            String normalizedUsername = username.trim();
            UserAccount sameUsername = userAccountDao.findByUsername(normalizedUsername);
            if (sameUsername != null
                    && sameUsername.getUserId() != null
                    && !currentUserId.trim().equalsIgnoreCase(sameUsername.getUserId().trim())) {
                return ServiceResult.failure("This username is already in use by another account.");
            }

            UserAccount updated = new UserAccount(
                    normalizedUsername,
                    existing.getPassword(),
                    existing.getRole(),
                    displayName.trim(),
                    existing.getUserId(),
                    firstNonBlank(existing.getAvatarPath(), ""),
                    existing.isFrozen());

            return persistAccount(updated, "Profile information updated successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save profile information.");
        }
    }

    public ServiceResult<LoginUser> updatePassword(String currentUserId, String newPassword, String confirmPassword) {
        if (isBlank(currentUserId)) {
            return ServiceResult.failure("The current login session is invalid. Please sign in again.");
        }
        if (isBlank(newPassword) || isBlank(confirmPassword)) {
            return ServiceResult.failure("Please complete both the new password and confirm password fields.");
        }
        if (!newPassword.trim().equals(confirmPassword.trim())) {
            return ServiceResult.failure("The two password entries do not match.");
        }
        if (newPassword.trim().length() < 6) {
            return ServiceResult.failure("The password must be at least 6 characters long.");
        }

        try {
            UserAccount existing = userAccountDao.findByUserId(currentUserId.trim());
            if (existing == null) {
                return ServiceResult.failure("The account to update could not be found.");
            }

            UserAccount updated = new UserAccount(
                    existing.getUsername(),
                    newPassword.trim(),
                    existing.getRole(),
                    existing.getDisplayName(),
                    existing.getUserId(),
                    firstNonBlank(existing.getAvatarPath(), ""),
                    existing.isFrozen());

            return persistAccount(updated, "Password updated successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save the password.");
        }
    }

    public ServiceResult<LoginUser> updateAvatar(String currentUserId, Part avatarPart, Path avatarUploadDir) {
        if (isBlank(currentUserId)) {
            return ServiceResult.failure("The current login session is invalid. Please sign in again.");
        }
        if (avatarPart == null || avatarPart.getSize() <= 0) {
            return ServiceResult.failure("Please choose an avatar image to upload.");
        }
        if (avatarUploadDir == null) {
            return ServiceResult.failure("The avatar upload directory is unavailable.");
        }

        String submittedFileName = avatarPart.getSubmittedFileName();
        if (!FileUploadUtil.isAllowedImageFile(submittedFileName)) {
            return ServiceResult.failure("Avatar files must be PNG, JPG, JPEG, GIF, or WEBP.");
        }

        try {
            UserAccount existing = userAccountDao.findByUserId(currentUserId.trim());
            if (existing == null) {
                return ServiceResult.failure("The account to update could not be found.");
            }

            String storedFileName = FileUploadUtil.buildStoredAvatarFileName(currentUserId.trim(), submittedFileName);
            Path targetFile = avatarUploadDir.resolve(storedFileName);
            deleteExistingAvatarFiles(currentUserId.trim(), avatarUploadDir, targetFile);
            FileUploadUtil.savePartToFile(avatarPart, targetFile);

            UserAccount updated = new UserAccount(
                    existing.getUsername(),
                    existing.getPassword(),
                    existing.getRole(),
                    existing.getDisplayName(),
                    existing.getUserId(),
                    "/uploads/avatars/" + storedFileName,
                    existing.isFrozen());

            return persistAccount(updated, "Avatar updated successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save the avatar.");
        }
    }

    private ServiceResult<LoginUser> persistAccount(UserAccount updated, String successMessage) throws IOException {
        UserAccount saved = userAccountDao.updateByUserId(updated);
        if (saved == null) {
            return ServiceResult.failure("Failed to save account information.");
        }
        return ServiceResult.success(toLoginUser(saved), successMessage);
    }

    private LoginUser toLoginUser(UserAccount account) {
        return new LoginUser(
                account.getUsername(),
                account.getRole(),
                account.getDisplayName(),
                account.getUserId(),
                account.getAvatarPath());
    }

    private void deleteExistingAvatarFiles(String userId, Path uploadDir, Path targetFile) throws IOException {
        if (isBlank(userId) || uploadDir == null) {
            return;
        }
        Files.createDirectories(uploadDir);
        String pattern = "avatar_" + userId.trim().replaceAll("[^A-Za-z0-9_-]", "_") + ".*";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir, pattern)) {
            for (Path path : stream) {
                if (!path.equals(targetFile)) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (!isBlank(preferred)) {
            return preferred.trim();
        }
        return fallback;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
