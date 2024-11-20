package com.hitachi.dronedelivery.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final Pattern VALID_CODE_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    public static boolean isValidName(String input) {
        if (input == null) {
            return false;
        }
        return VALID_NAME_PATTERN.matcher(input).matches();
    }

    public static boolean isValidCode(String input) {
        if (input == null) {
            return false;
        }
        return VALID_CODE_PATTERN.matcher(input).matches();
    }
}
