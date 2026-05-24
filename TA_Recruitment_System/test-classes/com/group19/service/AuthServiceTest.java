package com.group19.service;

import com.group19.TestRunner;
import com.group19.dao.UserAccountDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.UserAccount;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Unit tests for AuthService authentication service.
 * Uses a temporary JSON file as the data source for UserAccountDao.
 *
 * @author Group19
 */
public class AuthServiceTest extends TestRunner {

    private Path tempJsonFile;
    private UserAccountDao userAccountDao;
    private AuthService authService;

    /**
     * Creates a temporary JSON file and writes an empty array "[]" as initial data.
     */
    private Path createTempJsonFile() throws IOException {
        Path tempFile = Files.createTempFile("test-user-accounts-", ".json");
        Files.writeString(tempFile, "[]");
        return tempFile;
    }

    /**
     * Before each test method: creates a temporary JSON file and initialises DAO and service.
     */
    @Override
    protected void setUp() throws Exception {
        tempJsonFile = createTempJsonFile();
        userAccountDao = new UserAccountDao(tempJsonFile);
        // UserAccountDao automatically writes preset accounts (ta001, mo001, admin001, etc.) when the file is empty
        authService = new AuthService(userAccountDao);
    }

    /**
     * After each test method: deletes the temporary file.
     */
    @Override
    protected void tearDown() throws Exception {
        if (tempJsonFile != null) {
            Files.deleteIfExists(tempJsonFile);
        }
    }

    // ---- Helper methods ----

    /**
     * Adds a frozen account to the DAO for testing purposes.
     */
    private void addFrozenAccount() throws IOException {
        UserAccount frozen = new UserAccount(
                "frozenuser", "frozen123456", "TA",
                "Frozen User", "FROZEN001", "", true);
        userAccountDao.save(frozen);
    }

    // ==================== login tests ====================

    /** Login fails when the username is empty. */
    public void testLoginWithEmptyUsername() {
        ServiceResult<LoginUser> result = authService.login("", "password");
        assertFalse("Empty username should fail", result.isSuccess());
    }

    /** Login fails when the password is empty. */
    public void testLoginWithEmptyPassword() {
        ServiceResult<LoginUser> result = authService.login("ta001", "");
        assertFalse("Empty password should fail", result.isSuccess());
    }

    /** Login fails with invalid credentials (non-existent username). */
    public void testLoginWithInvalidCredentials() {
        ServiceResult<LoginUser> result = authService.login("nonexistent", "wrongpassword");
        assertFalse("Invalid credentials should fail", result.isSuccess());
    }

    /** Login succeeds with valid credentials. Preset account ta001 / ta123456. */
    public void testLoginWithValidCredentials() {
        ServiceResult<LoginUser> result = authService.login("ta001", "ta123456");
        assertTrue("Valid credentials should succeed", result.isSuccess());
        assertNotNull("LoginUser should not be null", result.getData());
        assertEquals("LoginUser username should match", "ta001", result.getData().getUsername());
        assertEquals("LoginUser role should match", "TA", result.getData().getRole());
    }

    /** Login fails for a frozen account. */
    public void testLoginWithFrozenAccount() throws IOException {
        addFrozenAccount();
        ServiceResult<LoginUser> result = authService.login("frozenuser", "frozen123456");
        assertFalse("Frozen account should fail to login", result.isSuccess());
    }

    /** Login fails when the specified role does not match the account's actual role.
     * mo001 is an MO role, but login is attempted as TA role. */
    public void testLoginWithWrongRole() {
        ServiceResult<LoginUser> result = authService.login("TA", "mo001", "mo123456");
        assertFalse("Wrong role should fail", result.isSuccess());
    }

    // ==================== register tests ====================

    /** Registration with valid data should succeed. */
    public void testRegisterWithValidData() {
        ServiceResult<LoginUser> result = authService.register(
                "TA", "Test User", "STU900", "newuser", "pass123456", "pass123456");
        assertTrue("Valid registration should succeed", result.isSuccess());
        assertNotNull("Registered LoginUser should not be null", result.getData());
    }

    /** Registration fails when the two passwords do not match. */
    public void testRegisterWithMismatchedPasswords() {
        ServiceResult<LoginUser> result = authService.register(
                "TA", "Test User", "STU901", "newuser2", "pass123456", "different");
        assertFalse("Mismatched passwords should fail", result.isSuccess());
    }

    /** Registration fails when the password is fewer than 6 characters. */
    public void testRegisterWithShortPassword() {
        ServiceResult<LoginUser> result = authService.register(
                "TA", "Test User", "STU902", "newuser3", "12345", "12345");
        assertFalse("Short password should fail", result.isSuccess());
    }

    /** Registration fails with a duplicate username. */
    public void testRegisterWithDuplicateUsername() {
        // First registration
        authService.register("TA", "User One", "STU903", "dupuser", "pass123456", "pass123456");
        // Second registration with the same username
        ServiceResult<LoginUser> result = authService.register(
                "TA", "User Two", "STU904", "dupuser", "pass123456", "pass123456");
        assertFalse("Duplicate username should fail", result.isSuccess());
    }

    /** Registration fails when required fields are empty. */
    public void testRegisterWithEmptyFields() {
        ServiceResult<LoginUser> result = authService.register(
                "TA", "", "STU905", "newuser5", "pass123456", "pass123456");
        assertFalse("Empty display name should fail", result.isSuccess());
    }

    // ==================== getPresetAccount tests ====================

    /** Passing a valid role "TA" should return a TA preset account. */
    public void testGetPresetAccountReturnsAccountForValidRole() {
        UserAccount account = authService.getPresetAccount("TA");
        assertNotNull("TA preset account should exist", account);
        assertEquals("Role should be TA", "TA", account.getRole());
    }

    /** Passing an invalid role should return null. */
    public void testGetPresetAccountReturnsNullForInvalidRole() {
        UserAccount account = authService.getPresetAccount("STUDENT");
        assertNull("Invalid role should return null", account);
    }

    // ==================== getAccountByUserId tests ====================

    /** Passing a valid userId should return the corresponding account.
     * The preset account ta001 has userId "231221618". */
    public void testGetAccountByUserIdReturnsAccountForValidId() {
        UserAccount account = authService.getAccountByUserId("231221618");
        assertNotNull("Account for valid userId should exist", account);
        assertEquals("Username should be ta001", "ta001", account.getUsername());
    }

    // ---- main ----

    public static void main(String[] args) {
        new AuthServiceTest().runTests();
    }
}
