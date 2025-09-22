package com.tarea.security;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class InputSanitizationUtils {
    private static final Pattern[] MALICIOUS_PATTERNS = new Pattern[]{
        Pattern.compile("<script", Pattern.CASE_INSENSITIVE),
        Pattern.compile("drop\\s*table", Pattern.CASE_INSENSITIVE),
        Pattern.compile("insert\\s+into", Pattern.CASE_INSENSITIVE),
        Pattern.compile("select\\s+.*\\s+from", Pattern.CASE_INSENSITIVE)
    };

    public static boolean containsMaliciousPattern(String input) {
        if (input == null) return false;
        for (Pattern pattern : MALICIOUS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }
        return false;
    }

     
    public static void validateAllStringFields(Object dto) {
        if (dto == null) return;
        for (Field field : dto.getClass().getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(dto);
                    if (containsMaliciousPattern(value)) {
                        throw new IllegalArgumentException(
                            "Malicious input detected in field: " + field.getName());
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
    }
}