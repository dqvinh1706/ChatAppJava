package com.chatapp.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean isValid(final String email) throws Exception {
        if (email.length() == 0) {
            throw new Exception("Field is required");
        }
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches())
            throw new Exception("Invalid email");
        return true;
    }
}
