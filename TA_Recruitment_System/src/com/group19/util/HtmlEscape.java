package com.group19.util;

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
