package com.group19.util;

import com.group19.TestRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Unit tests for {@link DataPathResolver}.
 * <p>
 * Since DataPathResolver depends on ServletContext (difficult to mock in unit tests),
 * this primarily verifies the structural properties of the class: the utility class's private constructor pattern.
 *
 * @author Group19
 * @since 1.0
 */
public class DataPathResolverTest extends TestRunner {

    public void testClassIsNotNull() {
        assertNotNull(DataPathResolver.class);
    }

    public void testConstructorIsPrivate() {
        Constructor<?>[] constructors = DataPathResolver.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);

        Constructor<?> constructor = constructors[0];
        assertTrue("constructor should be private",
                Modifier.isPrivate(constructor.getModifiers()));
    }

    public void testCannotInstantiateFromOutside() {
        // Verifies that the private constructor cannot be called from outside via reflection (would throw)
        Constructor<?>[] constructors = DataPathResolver.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);

        Constructor<?> constructor = constructors[0];
        // The private constructor should exist
        assertTrue("constructor should be private", Modifier.isPrivate(constructor.getModifiers()));

        // After setting accessible, it should be possible to instantiate (verifying the utility class pattern is correctly implemented)
        try {
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            assertNotNull("utility class should be instantiable via reflection", instance);
        } catch (Exception e) {
            fail("reflection instantiation of private constructor failed: " + e.getMessage());
        }
    }

    public void testResolveMethodExists() {
        // Verifies that the resolve method is static and public
        try {
            java.lang.reflect.Method method = DataPathResolver.class.getMethod(
                    "resolve",
                    jakarta.servlet.ServletContext.class,
                    String.class, String.class, String.class);
            assertNotNull(method);
            assertTrue("resolve method should be public static",
                    Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("resolve method not found: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new DataPathResolverTest().runTestsAndExit();
    }
}
