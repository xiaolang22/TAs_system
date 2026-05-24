package com.group19.filter;

import com.group19.TestRunner;

/**
 * Basic unit tests for AuthFilter, verifying class instantiation and lifecycle behaviour.
 * Since AuthFilter depends on the HTTP Servlet container (HttpServletRequest, etc.),
 * only the parts that do not depend on the container are tested here.
 *
 * @author Group19
 * @since 1.0
 */
public class AuthFilterTest extends TestRunner {

    private AuthFilter filter;

    @Override
    protected void setUp() throws Exception {
        filter = new AuthFilter();
    }

    @Override
    protected void tearDown() throws Exception {
        filter = null;
    }

    // ---------- Basic instantiation ----------

    /** Class is not null. */
    public void testClassIsNotNull() {
        assertNotNull("AuthFilter class should not be null", AuthFilter.class);
    }

    /** Constructor creates an instance. */
    public void testConstructorCreatesInstance() {
        AuthFilter instance = new AuthFilter();
        assertNotNull("constructor should create a non-null instance", instance);
        assertTrue("instance should be of type AuthFilter", instance instanceof AuthFilter);
    }

    // ---------- init ----------

    /** init() method does not throw. */
    public void testInitDoesNotThrow() {
        try {
            filter.init(null);
            assertTrue("init(null) should succeed", true);
        } catch (Exception e) {
            fail("init() should not throw: " + e.getMessage());
        }
    }

    // ---------- destroy ----------

    /** destroy() method does not throw. */
    public void testDestroyDoesNotThrow() {
        try {
            filter.destroy();
            assertTrue("destroy() should succeed", true);
        } catch (Exception e) {
            fail("destroy() should not throw: " + e.getMessage());
        }
    }

    // ---------- Multiple call verification ----------

    /** Multiple init/destroy cycles do not throw. */
    public void testMultipleInitDestroyCyclesAreSafe() {
        try {
            filter.init(null);
            filter.destroy();
            filter.init(null);
            filter.destroy();
            assertTrue("multiple init/destroy cycles should succeed", true);
        } catch (Exception e) {
            fail("multiple init/destroy should not throw: " + e.getMessage());
        }
    }

    // ---------- Filter interface implementation ----------

    /** AuthFilter implements the jakarta.servlet.Filter interface. */
    public void testImplementsFilterInterface() {
        assertTrue("AuthFilter should implement the Filter interface",
                filter instanceof jakarta.servlet.Filter);
    }

    public static void main(String[] args) {
        new AuthFilterTest().runTestsAndExit();
    }
}
