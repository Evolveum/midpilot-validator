package com.evolveum.validation.util;

import com.evolveum.validation.common.SupportedLanguage;

public class LanguageUtils {

    public static SupportedLanguage detectLanguage(String text) {
        String s = stripLeadingNoise(text);

        if (s.isEmpty()) return null;

        // XML
        if (s.startsWith("<")) {
            return SupportedLanguage.XML;
        }

        // JSON
        if (s.startsWith("{") || s.startsWith("[")) {
            return SupportedLanguage.JSON;
        }

        // YAML document start or YAML mapping key
        if (s.startsWith("---") || s.matches("^[A-Za-z_][\\w-]*\\s*:.*")) {
            return SupportedLanguage.YAML;
        }

        return null;
    }

    public static String stripLeadingNoise(String text) {
        int i = 0;
        int n = text.length();

        while (i < n) {
            // whitespace
            while (i < n && Character.isWhitespace(text.charAt(i))) {
                i++;
            }

            // comments
            if (i < n && text.charAt(i) == '#') {
                while (i < n && text.charAt(i) != '\n') i++;
                continue;
            }

            if (i + 3 < n && text.startsWith("<!--", i)) {
                int end = text.indexOf("-->", i + 4);
                if (end == -1) return "";
                i = end + 3;
                continue;
            }

            if (i + 1 < n && text.startsWith("//", i)) {
                while (i < n && text.charAt(i) != '\n') i++;
                continue;
            }

            if (i + 1 < n && text.startsWith("/*", i)) {
                int end = text.indexOf("*/", i + 2);
                if (end == -1) return "";
                i = end + 2;
                continue;
            }

            break;
        }

        return text.substring(i);
    }
}
