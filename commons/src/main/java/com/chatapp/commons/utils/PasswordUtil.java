package com.chatapp.commons.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private final int salt = 12;
    public final static String encode(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(12));
    }
    public final static boolean checkMatch(String raw, String encoded) {
        return BCrypt.checkpw(raw, encoded);
    }
}
