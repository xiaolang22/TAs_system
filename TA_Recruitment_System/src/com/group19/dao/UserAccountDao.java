package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.UserAccount;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDao {
    private static final List<UserAccount> DEFAULT_ACCOUNTS = Arrays.asList(
            new UserAccount("ta001", "ta123456", "TA", "TA Test User", "231221618"),
            new UserAccount("mo001", "mo123456", "MO", "MO Test User", "MO1001"),
            new UserAccount("admin001", "admin123456", "ADMIN", "Admin Test User", "ADMIN1001"));
    private final Path userFilePath;
    private final Type listType = new TypeToken<List<UserAccount>>() {
    }.getType();

    public UserAccountDao(Path userFilePath) {
        this.userFilePath = userFilePath;
    }

    public List<UserAccount> findAll() throws IOException {
        List<UserAccount> accounts = JsonFileUtil.readList(userFilePath, listType);
        if (!accounts.isEmpty()) {
            return accounts;
        }

        List<UserAccount> seededAccounts = new ArrayList<>();
        for (UserAccount account : DEFAULT_ACCOUNTS) {
            seededAccounts.add(new UserAccount(
                    account.getUsername(),
                    account.getPassword(),
                    account.getRole(),
                    account.getDisplayName(),
                    account.getUserId()));
        }
        JsonFileUtil.writeList(userFilePath, seededAccounts);
        return seededAccounts;
    }

    public UserAccount findByUsername(String username) throws IOException {
        if (username == null || username.isBlank()) {
            return null;
        }

        String normalized = username.trim();
        for (UserAccount account : findAll()) {
            if (normalized.equalsIgnoreCase(account.getUsername())) {
                return account;
            }
        }
        return null;
    }

    public UserAccount findByUserId(String userId) throws IOException {
        if (userId == null || userId.isBlank()) {
            return null;
        }

        String normalized = userId.trim();
        for (UserAccount account : findAll()) {
            if (normalized.equalsIgnoreCase(account.getUserId())) {
                return account;
            }
        }
        return null;
    }

    public UserAccount findFirstByRole(String role) throws IOException {
        if (role == null || role.isBlank()) {
            return null;
        }

        String normalized = role.trim();
        for (UserAccount account : findAll()) {
            if (normalized.equalsIgnoreCase(account.getRole())) {
                return account;
            }
        }
        return null;
    }

    public UserAccount save(UserAccount account) throws IOException {
        List<UserAccount> accounts = new ArrayList<>(findAll());
        accounts.add(account);
        JsonFileUtil.writeList(userFilePath, accounts);
        return account;
    }
}
