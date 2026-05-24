package com.group19.util;

/**
 * HTML special character escaping utility for XSS prevention.
 * Escapes characters such as &lt;, &gt;, &amp;, and quotation marks in
 * user-supplied text into their corresponding HTML entities to prevent
 * cross-site scripting attacks.
 *
 * @author Group19
 * @since 1.0
 */
public final class HtmlEscape {
    private HtmlEscape() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        String result = value.replace("&", "&amp;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        return result.replace("'", "&#39;");
    }
}
