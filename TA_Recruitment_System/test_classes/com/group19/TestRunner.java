package com.group19;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lightweight test framework providing JUnit-style assertions and test discovery.
 *
 * <p>Usage: extend TestRunner and define test methods (any public void method
 * whose name is prefixed with "test"). Override {@link #setUp()} and
 * {@link #tearDown()} for fixture management. Call {@link #runTests()} from
 * a {@code main} method to execute all test methods.
 *
 * <p>Example:
 * <pre>{@code
 * public class MyTest extends TestRunner {
 *     public void testAddition() {
 *         assertEquals(4, 2 + 2);
 *     }
 *
 *     public static void main(String[] args) {
 *         new MyTest().runTests();
 *     }
 * }
 * }</pre>
 *
 * @author Group19
 * @since 1.0
 */
public abstract class TestRunner {

    private int passed;
    private int failed;
    private final List<String> failures = new ArrayList<>();

    // ---- lifecycle hooks ----

    /**
     * Called before each test method. Override to prepare test fixtures.
     */
    protected void setUp() throws Exception {
        // default: no-op
    }

    /**
     * Called after each test method. Override to clean up test fixtures.
     */
    protected void tearDown() throws Exception {
        // default: no-op
    }

    // ---- assertion methods ----

    /**
     * Asserts that two values are equal (using {@link Object#equals}).
     */
    protected static void assertEquals(Object expected, Object actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two values are equal with a custom message.
     */
    protected static void assertEquals(String message, Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(
                    message == null
                            ? "expected <" + expected + "> but was <" + actual + ">"
                            : message + " — expected <" + expected + "> but was <" + actual + ">");
        }
    }

    /**
     * Asserts that two integers are equal.
     */
    protected static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    /**
     * Asserts that two doubles are equal within a positive delta.
     */
    protected static void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + "> (delta: " + delta + ")");
        }
    }

    /**
     * Asserts that a condition is true.
     */
    protected static void assertTrue(boolean condition) {
        assertTrue(null, condition);
    }

    /**
     * Asserts that a condition is true with a custom message.
     */
    protected static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message == null ? "expected true but was false" : message);
        }
    }

    /**
     * Asserts that a condition is false.
     */
    protected static void assertFalse(boolean condition) {
        assertFalse(null, condition);
    }

    /**
     * Asserts that a condition is false with a custom message.
     */
    protected static void assertFalse(String message, boolean condition) {
        if (condition) {
            throw new AssertionError(message == null ? "expected false but was true" : message);
        }
    }

    /**
     * Asserts that the given object is not null.
     */
    protected static void assertNotNull(Object obj) {
        assertNotNull(null, obj);
    }

    /**
     * Asserts that the given object is not null with a custom message.
     */
    protected static void assertNotNull(String message, Object obj) {
        if (obj == null) {
            throw new AssertionError(message == null ? "expected non-null value" : message);
        }
    }

    /**
     * Asserts that the given object is null.
     */
    protected static void assertNull(Object obj) {
        assertNull(null, obj);
    }

    /**
     * Asserts that the given object is null with a custom message.
     */
    protected static void assertNull(String message, Object obj) {
        if (obj != null) {
            throw new AssertionError(message == null ? "expected null but was <" + obj + ">" : message);
        }
    }

    /**
     * Asserts that the given code block throws an exception of the expected type.
     */
    protected static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable e) {
            if (expectedType.isInstance(e)) {
                return; // expected exception thrown
            }
            throw new AssertionError(
                    "expected " + expectedType.getName() + " but " + e.getClass().getName() + " was thrown", e);
        }
        throw new AssertionError("expected " + expectedType.getName() + " but nothing was thrown");
    }

    /**
     * Fails the test with the given message.
     */
    protected static void fail(String message) {
        throw new AssertionError(message == null ? "test failed" : message);
    }

    // ---- runner ----

    /**
     * Discovers and executes all test methods, printing a summary to stdout.
     * Test methods are public void instance methods whose names begin with "test".
     */
    public void runTests() {
        passed = 0;
        failed = 0;
        failures.clear();

        List<Method> testMethods = discoverTestMethods();

        System.out.println();
        System.out.println("=== " + getClass().getSimpleName() + " ===");
        System.out.println("Running " + testMethods.size() + " test(s)...");
        System.out.println();

        for (Method method : testMethods) {
            runTestMethod(method);
        }

        System.out.println();
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
        if (!failures.isEmpty()) {
            System.out.println();
            System.out.println("Failures:");
            for (String failure : failures) {
                System.out.println("  " + failure);
            }
        }
        System.out.println();
    }

    /**
     * Returns the total count of tests that have passed so far.
     */
    public int getPassedCount() {
        return passed;
    }

    /**
     * Returns the total count of tests that have failed so far.
     */
    public int getFailedCount() {
        return failed;
    }

    /**
     * Runs all tests and exits the JVM with code 0 on success, 1 on failure.
     */
    protected void runTestsAndExit() {
        runTests();
        if (failed > 0) {
            System.exit(1);
        }
    }

    private List<Method> discoverTestMethods() {
        List<Method> methods = new ArrayList<>();
        for (Method method : getClass().getDeclaredMethods()) {
            if (isTestMethod(method)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private boolean isTestMethod(Method method) {
        int mod = method.getModifiers();
        return java.lang.reflect.Modifier.isPublic(mod)
                && !java.lang.reflect.Modifier.isStatic(mod)
                && method.getReturnType() == void.class
                && method.getParameterCount() == 0
                && method.getName().startsWith("test");
    }

    private void runTestMethod(Method method) {
        try {
            setUp();
            method.invoke(this);
            tearDown();
            passed++;
            System.out.println("  [PASS] " + method.getName());
        } catch (AssertionError e) {
            failed++;
            String message = method.getName() + " — " + (e.getMessage() != null ? e.getMessage() : "(no message)");
            failures.add(message);
            System.out.println("  [FAIL] " + message);
        } catch (Exception e) {
            failed++;
            String cause = e.getCause() instanceof AssertionError ? e.getCause().getMessage() : e.toString();
            String message = method.getName() + " — " + (cause != null ? cause : "(no message)");
            failures.add(message);
            System.out.println("  [FAIL] " + message);
        }
    }

    // ---- functional interface ----

    /**
     * Runnable that may throw any throwable; used by {@link #assertThrows}.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    // ---- utility ----

    /**
     * Runs all test suites from the given main method.
     * Example: {@code TestRunner.runAll(ApplicationTest.class, JobTest.class);}
     */
    public static void runAll(Class<? extends TestRunner>... testClasses) {
        int totalPassed = 0;
        int totalFailed = 0;
        for (Class<? extends TestRunner> clazz : testClasses) {
            try {
                TestRunner suite = clazz.getDeclaredConstructor().newInstance();
                suite.runTests();
                totalPassed += suite.getPassedCount();
                totalFailed += suite.getFailedCount();
            } catch (Exception e) {
                System.err.println("Failed to load test suite: " + clazz.getName());
                e.printStackTrace();
            }
        }
        System.out.println("========================================");
        System.out.println("Total: " + (totalPassed + totalFailed) + " tests — "
                + totalPassed + " passed, " + totalFailed + " failed");
        System.out.println("========================================");
        if (totalFailed > 0) {
            System.exit(1);
        }
    }
}
