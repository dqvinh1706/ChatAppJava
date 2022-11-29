package com.chatapp.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator {
    private static final String USERNAME_PATTERN =
            "^[a-zA-Z]([._-](?![._-])|[a-zA-Z0-9])+[a-zA-z0-9]$";
    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    public static boolean isValid(final String username) throws Exception {
        if (username.length() < 5) {
            throw new Exception("Username should be at least 5 characters long.");
        }
        else if (!username.matches("\\S*")) {
            throw new Exception("Whitespace is not allowed.");
        }
        else if (!username.matches("^[a-zA-Z]+\\w+$")) {
            throw new Exception("Username must start with a letter.");
        }
        else if (!pattern.matcher(username).matches()){
            throw new Exception("Dot, hyphen, and underscore does not appear consecutively.");
        }

        return true;
    }
}
