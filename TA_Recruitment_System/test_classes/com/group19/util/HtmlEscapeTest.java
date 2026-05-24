package com.group19.util;

import com.group19.TestRunner;

/**
 * Unit tests for {@link HtmlEscape}.
 *
 * @author Group19
 * @since 1.0
 */
public class HtmlEscapeTest extends TestRunner {

    // ---- escape() null / empty / plain text ----

    public void testEscapeNullReturnsEmptyString() {
        assertEquals("", HtmlEscape.escape(null));
    }

    public void testEscapeEmptyStringReturnsEmptyString() {
        assertEquals("", HtmlEscape.escape(""));
    }

    public void testEscapePlainTextReturnsSameText() {
        String plain = "Hello World 123";
        assertEquals(plain, HtmlEscape.escape(plain));
    }

    public void testEscapePlainTextWithSpaces() {
        assertEquals("abc def", HtmlEscape.escape("abc def"));
    }

    // ---- individual special characters ----

    public void testEscapeConvertsAmpersand() {
        assertEquals("a &amp; b", HtmlEscape.escape("a & b"));
    }

    public void testEscapeConvertsLessThan() {
        assertEquals("a &lt; b", HtmlEscape.escape("a < b"));
    }

    public void testEscapeConvertsGreaterThan() {
        assertEquals("a &gt; b", HtmlEscape.escape("a > b"));
    }

    public void testEscapeConvertsDoubleQuote() {
        assertEquals("a &quot; b", HtmlEscape.escape("a \" b"));
    }

    public void testEscapeConvertsSingleQuote() {
        assertEquals("a &#39; b", HtmlEscape.escape("a ' b"));
    }

    // ---- combined special characters ----

    public void testEscapeCombinedSpecialCharacters() {
        String input = "<script>alert('XSS & \"attack\"');</script>";
        String expected = "&lt;script&gt;alert(&#39;XSS &amp; &quot;attack&quot;&#39;);&lt;/script&gt;";
        assertEquals(expected, HtmlEscape.escape(input));
    }

    public void testEscapeMultipleAmpersands() {
        assertEquals("&amp;&amp;&amp;", HtmlEscape.escape("&&&"));
    }

    public void testEscapeDoesNotDoubleEscape() {
        // The result after one escape should not be escaped again
        String once = HtmlEscape.escape("<&>");
        // An already-escaped string should not contain the original special characters
        assertFalse("escaped result should not contain original <", once.contains("<"));
        assertFalse("escaped result should not contain original >", once.contains(">"));
        assertFalse("escaped result should not contain original & (except in &amp;)", once.replace("&amp;", "").contains("&"));
    }

    // ---- ampersand ordering (important: & must be escaped first) ----

    public void testEscapeAmpersandOrdering() {
        // Verifies that & is escaped first, to avoid re-escaping the & in already-generated entities
        // (e.g., &lt; becoming &amp;lt;)
        String result = HtmlEscape.escape("<");
        assertEquals("&lt;", result);
    }

    public void testEscapeHtmlFragment() {
        String input = "<p class=\"intro\">Welcome to TA Recruitment & Selection</p>";
        String expected = "&lt;p class=&quot;intro&quot;&gt;Welcome to TA Recruitment &amp; Selection&lt;/p&gt;";
        assertEquals(expected, HtmlEscape.escape(input));
    }

    public static void main(String[] args) {
        new HtmlEscapeTest().runTestsAndExit();
    }
}
