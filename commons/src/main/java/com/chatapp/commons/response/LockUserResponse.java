package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@Getter
public class LockUserResponse extends Response {
    private String notification;
    @Builder
    public LockUserResponse(@NonNull StatusCode statusCode, String notification) {
        super(statusCode);
        this.notification = notification;
    }
}