package com.chatapp.validations;

import net.synedra.validatorfx.Check.Context;

public class PasswordValidator {
    public static boolean isValid(final String password) throws Exception {
        if (password.length() < 8) {
            throw new Exception("Password should be at least 8 characters long.");
        }
        else if (!password.matches("\\S*")) {
            throw new Exception("Whitespace is not allowed.");
        }
        return true;
    }
}
