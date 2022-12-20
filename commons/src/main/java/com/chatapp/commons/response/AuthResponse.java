package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AuthResponse extends Response {
    private Exception err;
    private User user;

    @Builder
    public AuthResponse(@NonNull StatusCode statusCode, User user, Exception err) {
        super(statusCode);
        this.err = err;
        this.user = user;
    }
}
