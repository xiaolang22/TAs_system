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

public class AccountCenterService {
    private final UserAccountDao userAccountDao;

    public AccountCenterService(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    public ServiceResult<UserAccount> loadAccount(String userId) {
        if (isBlank(userId)) {
            return ServiceResult.failure("无法加载账号信息。");
        }
        try {
            UserAccount account = userAccountDao.findByUserId(userId.trim());
            if (account == null) {
                return ServiceResult.failure("未找到当前账号。");
            }
            return ServiceResult.success(account, "账号信息加载成功。");
        } catch (IOException e) {
            return ServiceResult.failure("读取账号信息失败。");
        }
    }

    public ServiceResult<LoginUser> updateProfile(String currentUserId, String displayName, String username) {
        if (isBlank(currentUserId)) {
            return ServiceResult.failure("当前登录信息无效，请重新登录。");
        }
        if (isBlank(displayName) || isBlank(username)) {
            return ServiceResult.failure("姓名和账号不能为空。");
        }

        try {
            UserAccount existing = userAccountDao.findByUserId(currentUserId.trim());
            if (existing == null) {
                return ServiceResult.failure("未找到需要更新的账号。");
            }

            String normalizedUsername = username.trim();
            UserAccount sameUsername = userAccountDao.findByUsername(normalizedUsername);
            if (sameUsername != null
                    && sameUsername.getUserId() != null
                    && !currentUserId.trim().equalsIgnoreCase(sameUsername.getUserId().trim())) {
                return ServiceResult.failure("该账号已被其他用户使用。");
            }

            UserAccount updated = new UserAccount(
                    normalizedUsername,
                    existing.getPassword(),
                    existing.getRole(),
                    displayName.trim(),
                    existing.getUserId(),
                    firstNonBlank(existing.getAvatarPath(), ""),
                    existing.isFrozen());

            return persistAccount(updated, "个人信息已更新。");
        } catch (IOException e) {
            return ServiceResult.failure("保存个人信息失败。");
        }
    }

    public ServiceResult<LoginUser> updatePassword(String currentUserId, String newPassword, String confirmPassword) {
        if (isBlank(currentUserId)) {
            return ServiceResult.failure("当前登录信息无效，请重新登录。");
        }
        if (isBlank(newPassword) || isBlank(confirmPassword)) {
            return ServiceResult.failure("请完整填写新密码和确认密码。");
        }
        if (!newPassword.trim().equals(confirmPassword.trim())) {
            return ServiceResult.failure("两次输入的密码不一致。");
        }
        if (newPassword.trim().length() < 6) {
            return ServiceResult.failure("密码长度不能少于 6 位。");
        }

        try {
            UserAccount existing = userAccountDao.findByUserId(currentUserId.trim());
            if (existing == null) {
                return ServiceResult.failure("未找到需要更新的账号。");
            }

            UserAccount updated = new UserAccount(
                    existing.getUsername(),
                    newPassword.trim(),
                    existing.getRole(),
                    existing.getDisplayName(),
                    existing.getUserId(),
                    firstNonBlank(existing.getAvatarPath(), ""),
                    existing.isFrozen());

            return persistAccount(updated, "密码已更新。");
        } catch (IOException e) {
            return ServiceResult.failure("保存密码失败。");
        }
    }

    public ServiceResult<LoginUser> updateAvatar(String currentUserId, Part avatarPart, Path avatarUploadDir) {
        if (isBlank(currentUserId)) {
            return ServiceResult.failure("当前登录信息无效，请重新登录。");
        }
        if (avatarPart == null || avatarPart.getSize() <= 0) {
            return ServiceResult.failure("请选择要上传的头像图片。");
        }
        if (avatarUploadDir == null) {
            return ServiceResult.failure("头像上传目录不可用。");
        }

        String submittedFileName = avatarPart.getSubmittedFileName();
        if (!FileUploadUtil.isAllowedImageFile(submittedFileName)) {
            return ServiceResult.failure("头像仅支持 PNG、JPG、JPEG、GIF 或 WEBP 格式。");
        }

        try {
            UserAccount existing = userAccountDao.findByUserId(currentUserId.trim());
            if (existing == null) {
                return ServiceResult.failure("未找到需要更新的账号。");
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

            return persistAccount(updated, "头像已更新。");
        } catch (IOException e) {
            return ServiceResult.failure("保存头像失败。");
        }
    }

    private ServiceResult<LoginUser> persistAccount(UserAccount updated, String successMessage) throws IOException {
        UserAccount saved = userAccountDao.updateByUserId(updated);
        if (saved == null) {
            return ServiceResult.failure("保存账号信息失败。");
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
