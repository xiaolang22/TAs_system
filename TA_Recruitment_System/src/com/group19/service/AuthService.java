package com.group19.service;

import com.group19.dao.UserAccountDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.UserAccount;
import java.io.IOException;

public class AuthService {
    private final UserAccountDao userAccountDao;

    public AuthService(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    public ServiceResult<LoginUser> login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return ServiceResult.failure("Please enter both username and password.");
        }

        try {
            UserAccount account = userAccountDao.findByUsername(username.trim());
            if (account == null || !password.trim().equals(account.getPassword())) {
                return ServiceResult.failure("Invalid username or password.");
            }

            LoginUser loginUser = new LoginUser(
                    account.getUsername(),
                    account.getRole(),
                    account.getDisplayName(),
                    account.getUserId());

            return ServiceResult.success(loginUser, "Login successful.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to load account data.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
