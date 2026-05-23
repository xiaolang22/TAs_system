package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.UserAccount;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDao {
    private static final List<UserAccount> DEFAULT_ACCOUNTS = buildDefaultAccounts();
    private final Path userFilePath;
    private final Type listType = new TypeToken<List<UserAccount>>() {
    }.getType();

    public UserAccountDao(Path userFilePath) {
        this.userFilePath = userFilePath;
    }

    private static List<UserAccount> buildDefaultAccounts() {
        List<UserAccount> accounts = new ArrayList<>();
        accounts.add(new UserAccount("ta001", "ta123456", "TA", "TA Test User", "231221618"));
        accounts.add(new UserAccount("ta099", "ta123456", "TA", "US09 tester1", "231221619"));
        accounts.add(new UserAccount("ta100", "ta123456", "TA", "US09 tester2", "231221620"));
        String[] taNames = {
                "Liang Chen", "Yutong Zhao", "Mia Sun", "Haoran Wu", "Iris Gao", "Zexin Hu", "Cindy Wang",
                "Leo Qian", "Joy Xu", "Victor He", "Fiona Lin", "Owen Yu", "Nina Deng", "Kevin Luo",
                "Alice Zhou", "Martin Peng", "Grace Shen", "Ethan Xie", "Sophie Tang", "Daniel Ma",
                "Clara Jiang", "Ryan Cai", "Selina Yao", "Jason Guo", "Amber Fan", "Felix Dai",
                "Stella Mo", "Aaron Hou", "Vivian Nie", "Oscar Ren", "Bella Kong", "Ian Song",
                "Chloe Lu", "Mason Jin", "Tina Bai", "Eric Su", "Helen Zou", "Noah Fang",
                "Doris Yan", "Simon Qu", "Elsa Tao", "Gavin Liao", "Maggie Pei", "Harvey Zhu",
                "Janice Ke", "Tristan Han", "Wendy Shi", "Colin Fu", "Queenie Ruan", "Shawn Lei",
                "Theresa Du", "Bruce Wen", "Olivia Ge", "Neil Pan", "Yvonne Cheng", "Aiden Yuan",
                "Rita Meng"
        };
        for (int i = 0; i < taNames.length; i++) {
            String username = "ta" + String.format("%03d", i + 2);
            String userId = String.valueOf(231221621 + i);
            accounts.add(new UserAccount(username, "ta123456", "TA", taNames[i], userId));
        }
        accounts.add(new UserAccount("mo001", "mo123456", "MO", "MO 01", "MO1001"));
        for (int i = 2; i <= 20; i++) {
            String index = String.format("%03d", i);
            String displayIndex = String.format("%02d", i);
            accounts.add(new UserAccount("mo" + index, "mo123456", "MO", "MO " + displayIndex, "MO10" + String.format("%02d", i)));
        }
        accounts.add(new UserAccount("admin001", "admin123456", "ADMIN", "Admin Test User", "ADMIN1001"));
        return accounts;
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
                    account.getUserId(),
                    account.getAvatarPath(),
                    account.isFrozen()));
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

    public UserAccount updateByUserId(UserAccount target) throws IOException {
        if (target == null || target.getUserId() == null || target.getUserId().isBlank()) {
            return null;
        }

        List<UserAccount> accounts = new ArrayList<>(findAll());
        String normalizedUserId = target.getUserId().trim();
        for (int i = 0; i < accounts.size(); i++) {
            UserAccount current = accounts.get(i);
            if (current.getUserId() != null && normalizedUserId.equalsIgnoreCase(current.getUserId().trim())) {
                accounts.set(i, target);
                JsonFileUtil.writeList(userFilePath, accounts);
                return target;
            }
        }
        return null;
    }
}
