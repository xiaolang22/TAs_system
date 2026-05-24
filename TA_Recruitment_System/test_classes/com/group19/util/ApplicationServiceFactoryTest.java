package com.group19.util;

import com.group19.TestRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Unit tests for {@link ApplicationServiceFactory}.
 * <p>
 * Since ApplicationServiceFactory depends on ServletContext (difficult to mock in unit tests),
 * this primarily verifies the structural properties of the class: the factory pattern's private constructor.
 *
 * @author Group19
 * @since 1.0
 */
public class ApplicationServiceFactoryTest extends TestRunner {

    public void testClassExistsAndIsNotNull() {
        assertNotNull(ApplicationServiceFactory.class);
    }

    public void testConstructorIsPrivate() {
        Constructor<?>[] constructors = ApplicationServiceFactory.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);

        Constructor<?> constructor = constructors[0];
        assertTrue("constructor should be private",
                Modifier.isPrivate(constructor.getModifiers()));
    }

    public void testCannotInstantiateDirectly() {
        // Verifies that the private constructor can be invoked via reflection (correct factory pattern implementation)
        Constructor<?>[] constructors = ApplicationServiceFactory.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);

        Constructor<?> constructor = constructors[0];
        assertTrue("constructor should be private", Modifier.isPrivate(constructor.getModifiers()));

        try {
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            assertNotNull("factory class should be instantiable via reflection", instance);
        } catch (Exception e) {
            fail("reflection instantiation of private constructor failed: " + e.getMessage());
        }
    }

    public void testCreateMethodExists() {
        // Verifies that the create method is static and public
        try {
            java.lang.reflect.Method method = ApplicationServiceFactory.class.getMethod(
                    "create",
                    jakarta.servlet.ServletContext.class);
            assertNotNull(method);
            assertTrue("create method should be public static",
                    Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("create method not found: " + e.getMessage());
        }
    }

    public void testClassIsFinalUtilityClass() {
        // Verifies that the class is final and cannot be extended
        assertTrue("ApplicationServiceFactory should be a final class",
                Modifier.isFinal(ApplicationServiceFactory.class.getModifiers()));
    }

    public static void main(String[] args) {
        new ApplicationServiceFactoryTest().runTestsAndExit();
    }
}
