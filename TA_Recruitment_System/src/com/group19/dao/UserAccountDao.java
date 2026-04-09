package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.UserAccount;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class UserAccountDao {
    private final Path userFilePath;
    private final Type listType = new TypeToken<List<UserAccount>>() {
    }.getType();

    public UserAccountDao(Path userFilePath) {
        this.userFilePath = userFilePath;
    }

    public List<UserAccount> findAll() throws IOException {
        return JsonFileUtil.readList(userFilePath, listType);
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
}
