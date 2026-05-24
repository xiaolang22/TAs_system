package com.group19.model;

import com.group19.TestRunner;
import com.group19.model.LoginUser;

/**
 * Unit tests for the LoginUser model class (immutable).
 * Tests constructors, all 5 getters, and verifies immutability (no setters).
 *
 * @author Group19
 * @since 1.0
 */
public class LoginUserTest extends TestRunner {

    public void testConstructorWithBasicFieldsCreatesCorrectly() {
        LoginUser user = new LoginUser("ta_user", "TA", "Alice Johnson", "STU001");
        assertNotNull("LoginUser should be created", user);
        assertEquals("ta_user", user.getUsername());
        assertEquals("TA", user.getRole());
        assertEquals("Alice Johnson", user.getDisplayName());
        assertEquals("STU001", user.getUserId());
        assertEquals("", user.getAvatarPath());
    }

    public void testConstructorWithAvatarPathCreatesCorrectly() {
        LoginUser user = new LoginUser("mo_user", "MO", "Bob Smith", "MO001", "/avatars/mo.png");
        assertNotNull("LoginUser should be created", user);
        assertEquals("mo_user", user.getUsername());
        assertEquals("MO", user.getRole());
        assertEquals("Bob Smith", user.getDisplayName());
        assertEquals("MO001", user.getUserId());
        assertEquals("/avatars/mo.png", user.getAvatarPath());
    }

    public void testGetUsernameReturnsCorrectValue() {
        LoginUser user = new LoginUser("admin_user", "ADMIN", "管理员", "ADM001");
        assertEquals("admin_user", user.getUsername());
    }

    public void testGetRoleReturnsCorrectValue() {
        LoginUser user = new LoginUser("ta_user", "TA", "Alice Johnson", "STU001");
        assertEquals("TA", user.getRole());
    }

    public void testGetDisplayNameReturnsCorrectValue() {
        LoginUser user = new LoginUser("ta_user", "TA", "Charlie Brown", "STU002");
        assertEquals("Charlie Brown", user.getDisplayName());
    }

    public void testGetUserIdReturnsCorrectValue() {
        LoginUser user = new LoginUser("ta_user", "TA", "Alice Johnson", "STU999");
        assertEquals("STU999", user.getUserId());
    }

    public void testGetAvatarPathReturnsCorrectValue() {
        LoginUser user = new LoginUser("ta_user", "TA", "Alice Johnson", "STU001", "/path/to/avatar.jpg");
        assertEquals("/path/to/avatar.jpg", user.getAvatarPath());
    }

    public void testImmutabilityNoSettersExist() {
        // LoginUser has no setter methods — verify by checking that all fields are final
        // We test this indirectly: the class should have no methods starting with "set"
        LoginUser user = new LoginUser("test", "TA", "Test", "ID001");
        java.lang.reflect.Method[] methods = LoginUser.class.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            if (method.getName().startsWith("set")) {
                fail("LoginUser should be immutable but found setter: " + method.getName());
            }
        }
        assertNotNull("Immutability check passed", user);
    }

    public void testConstructorWithFourArgsDelegatesToFiveArgs() {
        // The 4-arg constructor sets avatarPath to empty string by delegating to 5-arg constructor
        LoginUser user = new LoginUser("user1", "TA", "Test User", "ID001");
        assertEquals("user1", user.getUsername());
        assertEquals("TA", user.getRole());
        assertEquals("Test User", user.getDisplayName());
        assertEquals("ID001", user.getUserId());
        assertEquals("", user.getAvatarPath());
    }

    public static void main(String[] args) {
        new LoginUserTest().runTestsAndExit();
    }
}
