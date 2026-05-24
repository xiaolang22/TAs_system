package com.group19.dao;

import com.google.gson.reflect.TypeToken;
import com.group19.model.UserAccount;
import com.group19.util.JsonFileUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for UserAccount entities, responsible for reading and writing
 * user account data stored in JSON files. Upon first access, if the data file is
 * empty, the DAO automatically initialises it with preset accounts (including TA,
 * MO, and ADMIN test accounts).
 *
 * @author Group19
 * @since 1.0
 */
public class UserAccountDao {

    /** Preset list of default accounts used for first-time initialisation. */
    private static final List<UserAccount> DEFAULT_ACCOUNTS = buildDefaultAccounts();

    /** File path to the user account data JSON file. */
    private final Path userFilePath;

    /** Type token required for Gson deserialisation. */
    private final Type listType = new TypeToken<List<UserAccount>>() {
    }.getType();

    /**
     * Constructs a new UserAccountDao instance.
     *
     * @param userFilePath file path to the user account data JSON file
     */
    public UserAccountDao(Path userFilePath) {
        this.userFilePath = userFilePath;
    }

    /**
     * Builds the system's default account list, including TA test accounts, MO test
     * accounts, and an administrator account.
     *
     * @return a list of preset accounts
     */
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

    /**
     * Retrieves all user accounts (automatically initialises with default data if the
     * file is empty on first call).
     *
     * @return a list of user accounts
     * @throws IOException if the file cannot be read or written
     */
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

    /**
     * Finds a user account by username (case-insensitive).
     *
     * @param username the username
     * @return the matching UserAccount, or {@code null} if not found
     * @throws IOException if the file cannot be read
     */
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

    /**
     * Finds a user account by user ID (case-insensitive).
     *
     * @param userId the unique user identifier (student/staff ID)
     * @return the matching UserAccount, or {@code null} if not found
     * @throws IOException if the file cannot be read
     */
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

    /**
     * Finds the first user with the given role (case-insensitive).
     *
     * @param role the role name (TA, MO, or ADMIN)
     * @return the matching UserAccount, or {@code null} if not found
     * @throws IOException if the file cannot be read
     */
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

    /**
     * Saves a new user account (appends to the JSON file).
     *
     * @param account the UserAccount object to save
     * @return the saved UserAccount object
     * @throws IOException if the file cannot be written
     */
    public UserAccount save(UserAccount account) throws IOException {
        List<UserAccount> accounts = new ArrayList<>(findAll());
        accounts.add(account);
        JsonFileUtil.writeList(userFilePath, accounts);
        return account;
    }

    /**
     * Updates a user account by user ID (case-insensitive ID matching; replaces if
     * found).
     *
     * @param target the UserAccount object containing updated data
     * @return the updated UserAccount, or {@code null} if no matching record is found
     * @throws IOException if the file cannot be written
     */
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
