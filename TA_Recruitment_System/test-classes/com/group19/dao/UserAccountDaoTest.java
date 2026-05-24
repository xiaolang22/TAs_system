package com.group19.dao;

import com.group19.TestRunner;
import com.group19.model.UserAccount;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for UserAccountDao. Uses temporary JSON files for data read/write verification.
 *
 * @author Group19
 * @since 1.0
 */
public class UserAccountDaoTest extends TestRunner {

    private Path tempFile;
    private UserAccountDao dao;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("test-users-", ".json");
        Files.writeString(tempFile, "[]");
        dao = new UserAccountDao(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    // ---------- findAll ----------

    /** findAll() automatically seeds default accounts when the file is empty. */
    public void testFindAllSeedsDefaultAccountsWhenFileIsEmpty() throws Exception {
        List<UserAccount> result = dao.findAll();
        assertNotNull("result should not be null", result);
        assertFalse("should contain default accounts", result.isEmpty());
        // Default accounts should include ta001, mo001, admin001, etc.
        boolean hasAdmin = result.stream().anyMatch(a -> "ADMIN".equals(a.getRole()));
        boolean hasTA = result.stream().anyMatch(a -> "TA".equals(a.getRole()));
        boolean hasMO = result.stream().anyMatch(a -> "MO".equals(a.getRole()));
        assertTrue("should contain ADMIN role account", hasAdmin);
        assertTrue("should contain TA role account", hasTA);
        assertTrue("should contain MO role account", hasMO);
    }

    /** A second call to findAll() returns the seeded accounts without duplicating seeding. */
    public void testFindAllDoesNotDuplicateSeeding() throws Exception {
        List<UserAccount> first = dao.findAll();
        int count = first.size();
        List<UserAccount> second = dao.findAll();
        assertEquals("second call should return the same number of accounts", count, second.size());
    }

    // ---------- findByUsername ----------

    /** findByUsername() finds an existing user. */
    public void testFindByUsernameFindsExistingUser() throws Exception {
        dao.findAll(); // ensure seed data is written

        UserAccount found = dao.findByUsername("ta001");
        assertNotNull("should find ta001", found);
        assertEquals("TA", found.getRole());

        // Case-insensitive
        UserAccount foundCase = dao.findByUsername("TA001");
        assertNotNull("case-insensitive lookup should match", foundCase);
    }

    /** findByUsername() returns null for a non-existent username. */
    public void testFindByUsernameReturnsNullForNonExistent() throws Exception {
        dao.findAll();
        UserAccount found = dao.findByUsername("nonexistent_user");
        assertNull("non-existent username should return null", found);
    }

    // ---------- findByUserId ----------

    /** findByUserId() finds an existing user. */
    public void testFindByUserIdFindsExistingUser() throws Exception {
        dao.findAll();
        UserAccount found = dao.findByUserId("231221618");
        assertNotNull("should find userId=231221618", found);
        assertEquals("ta001", found.getUsername());
    }

    // ---------- findFirstByRole ----------

    /** findFirstByRole() finds the first user with the given role. */
    public void testFindFirstByRoleFindsFirstUserWithGivenRole() throws Exception {
        dao.findAll();

        UserAccount ta = dao.findFirstByRole("TA");
        assertNotNull("should find a TA role user", ta);
        assertEquals("TA", ta.getRole());

        UserAccount admin = dao.findFirstByRole("ADMIN");
        assertNotNull("should find an ADMIN role user", admin);
        assertEquals("ADMIN", admin.getRole());
    }

    // ---------- save ----------

    /** save() can add a new user. */
    public void testSaveAddsNewUser() throws Exception {
        dao.findAll(); // seed first
        int before = dao.findAll().size();

        UserAccount newUser = new UserAccount("newuser", "pass123", "TA", "新用户", "USER-999");
        UserAccount saved = dao.save(newUser);
        assertNotNull("should return the saved user", saved);
        assertEquals("newuser", saved.getUsername());

        List<UserAccount> all = dao.findAll();
        assertEquals("count should increase by 1", before + 1, all.size());
    }

    // ---------- updateByUserId ----------

    /** updateByUserId() updates an existing user. */
    public void testUpdateByUserIdUpdatesExistingUser() throws Exception {
        dao.findAll();
        UserAccount target = new UserAccount("ta001", "newpass", "TA", "Updated TA", "231221618",
                "/avatars/new.png", true);
        UserAccount result = dao.updateByUserId(target);
        assertNotNull("update should return non-null", result);
        assertEquals("newpass", result.getPassword());
        assertEquals("Updated TA", result.getDisplayName());
        assertTrue("should be marked as frozen", result.isFrozen());

        // Verify persistence
        UserAccount reloaded = dao.findByUserId("231221618");
        assertEquals("password should be updated after persistence", "newpass", reloaded.getPassword());
        assertEquals("display name should be updated after persistence", "Updated TA", reloaded.getDisplayName());
    }

    /** updateByUserId() returns null for a null target. */
    public void testUpdateByUserIdReturnsNullForNullTarget() throws Exception {
        dao.findAll();
        UserAccount result = dao.updateByUserId(null);
        assertNull("null argument should return null", result);
    }

    public static void main(String[] args) {
        new UserAccountDaoTest().runTestsAndExit();
    }
}
