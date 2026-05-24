package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.UserAccount;

/**
 * Unit tests for the UserAccount model class.
 * Tests default constructor, three parameterised constructors (5/6/7 args),
 * and all 7 getter/setter pairs including boolean isFrozen.
 *
 * @author Group19
 * @since 1.0
 */
public class UserAccountTest extends TestRunner {

    public void testDefaultConstructorInitialisesAllFieldsToNullOrDefault() {
        UserAccount account = new UserAccount();
        assertNotNull("Default constructor should create a non-null UserAccount object", account);
        assertNull("username should be null after default constructor", account.getUsername());
        assertNull("password should be null after default constructor", account.getPassword());
        assertNull("role should be null after default constructor", account.getRole());
        assertNull("displayName should be null after default constructor", account.getDisplayName());
        assertNull("userId should be null after default constructor", account.getUserId());
        assertNull("avatarPath should be null after default constructor", account.getAvatarPath());
        assertFalse("isFrozen should be false after default constructor", account.isFrozen());
    }

    public void testConstructorWithFiveArgsSetsBasicFieldsAndDefaultsFrozenToFalse() {
        UserAccount account = new UserAccount("ta_user", "pass123", "TA", "Alice Johnson", "STU001");
        assertNotNull("Constructor should create a non-null UserAccount object", account);
        assertEquals("ta_user", account.getUsername());
        assertEquals("pass123", account.getPassword());
        assertEquals("TA", account.getRole());
        assertEquals("Alice Johnson", account.getDisplayName());
        assertEquals("STU001", account.getUserId());
        assertEquals("", account.getAvatarPath());
        assertFalse("isFrozen should be false by default", account.isFrozen());
    }

    public void testConstructorWithSixArgsSetsAllFieldsAndDefaultsFrozenToFalse() {
        UserAccount account = new UserAccount("mo_user", "pass456", "MO", "Bob Smith", "MO001", "/avatars/mo.png");
        assertNotNull("Constructor should create a non-null UserAccount object", account);
        assertEquals("mo_user", account.getUsername());
        assertEquals("pass456", account.getPassword());
        assertEquals("MO", account.getRole());
        assertEquals("Bob Smith", account.getDisplayName());
        assertEquals("MO001", account.getUserId());
        assertEquals("/avatars/mo.png", account.getAvatarPath());
        assertFalse("isFrozen should be false by default", account.isFrozen());
    }

    public void testConstructorWithSevenArgsSetsAllFieldsIncludingFrozenTrue() {
        UserAccount account = new UserAccount("admin_user", "admin123", "ADMIN", "管理员", "ADM001", "/avatars/admin.png", true);
        assertNotNull("Constructor should create a non-null UserAccount object", account);
        assertEquals("admin_user", account.getUsername());
        assertEquals("admin123", account.getPassword());
        assertEquals("ADMIN", account.getRole());
        assertEquals("管理员", account.getDisplayName());
        assertEquals("ADM001", account.getUserId());
        assertEquals("/avatars/admin.png", account.getAvatarPath());
        assertTrue("isFrozen should be true when explicitly set", account.isFrozen());
    }

    public void testConstructorWithSevenArgsSetsAllFieldsIncludingFrozenFalse() {
        UserAccount account = new UserAccount("ta_user2", "pass789", "TA", "Charlie Brown", "STU002", "", false);
        assertEquals("ta_user2", account.getUsername());
        assertEquals("pass789", account.getPassword());
        assertEquals("TA", account.getRole());
        assertEquals("Charlie Brown", account.getDisplayName());
        assertEquals("STU002", account.getUserId());
        assertEquals("", account.getAvatarPath());
        assertFalse("isFrozen should be false", account.isFrozen());
    }

    public void testSetAndGetUsername() {
        UserAccount account = new UserAccount();
        account.setUsername("new_user");
        assertEquals("new_user", account.getUsername());
    }

    public void testSetAndGetPassword() {
        UserAccount account = new UserAccount();
        account.setPassword("newpass");
        assertEquals("newpass", account.getPassword());
    }

    public void testSetAndGetRole() {
        UserAccount account = new UserAccount();
        account.setRole("TA");
        assertEquals("TA", account.getRole());
    }

    public void testSetAndGetDisplayName() {
        UserAccount account = new UserAccount();
        account.setDisplayName("赵六");
        assertEquals("赵六", account.getDisplayName());
    }

    public void testSetAndGetUserId() {
        UserAccount account = new UserAccount();
        account.setUserId("STU010");
        assertEquals("STU010", account.getUserId());
    }

    public void testSetAndGetAvatarPath() {
        UserAccount account = new UserAccount();
        account.setAvatarPath("/images/profile.png");
        assertEquals("/images/profile.png", account.getAvatarPath());
    }

    public void testSetAndIsFrozen() {
        UserAccount account = new UserAccount();
        assertFalse("isFrozen should default to false", account.isFrozen());
        account.setFrozen(true);
        assertTrue("isFrozen should be true after setFrozen(true)", account.isFrozen());
        account.setFrozen(false);
        assertFalse("isFrozen should be false after setFrozen(false)", account.isFrozen());
    }

    public void testAllFieldsSetAndGetConsistently() {
        UserAccount account = new UserAccount();
        account.setUsername("test_user");
        account.setPassword("test_pass");
        account.setRole("MO");
        account.setDisplayName("测试用户");
        account.setUserId("MO999");
        account.setAvatarPath("/images/test.png");
        account.setFrozen(true);

        assertEquals("test_user", account.getUsername());
        assertEquals("test_pass", account.getPassword());
        assertEquals("MO", account.getRole());
        assertEquals("测试用户", account.getDisplayName());
        assertEquals("MO999", account.getUserId());
        assertEquals("/images/test.png", account.getAvatarPath());
        assertTrue("isFrozen should be true", account.isFrozen());
    }

    public void testFiveArgConstructorDelegatesToFullConstructor() {
        // Verify 5-arg creates same result as 7-arg with empty avatar and frozen=false
        UserAccount account5 = new UserAccount("user", "pass", "TA", "Name", "ID");
        UserAccount account7 = new UserAccount("user", "pass", "TA", "Name", "ID", "", false);
        assertEquals(account7.getUsername(), account5.getUsername());
        assertEquals(account7.getPassword(), account5.getPassword());
        assertEquals(account7.getRole(), account5.getRole());
        assertEquals(account7.getDisplayName(), account5.getDisplayName());
        assertEquals(account7.getUserId(), account5.getUserId());
        assertEquals(account7.getAvatarPath(), account5.getAvatarPath());
        assertEquals(account7.isFrozen(), account5.isFrozen());
    }

    public void testSixArgConstructorDelegatesToFullConstructor() {
        // Verify 6-arg creates same result as 7-arg with frozen=false
        UserAccount account6 = new UserAccount("user", "pass", "TA", "Name", "ID", "/avatar.png");
        UserAccount account7 = new UserAccount("user", "pass", "TA", "Name", "ID", "/avatar.png", false);
        assertEquals(account7.getUsername(), account6.getUsername());
        assertEquals(account7.getPassword(), account6.getPassword());
        assertEquals(account7.getRole(), account6.getRole());
        assertEquals(account7.getDisplayName(), account6.getDisplayName());
        assertEquals(account7.getUserId(), account6.getUserId());
        assertEquals(account7.getAvatarPath(), account6.getAvatarPath());
        assertEquals(account7.isFrozen(), account6.isFrozen());
    }

    public static void main(String[] args) {
        new UserAccountTest().runTestsAndExit();
    }
}
