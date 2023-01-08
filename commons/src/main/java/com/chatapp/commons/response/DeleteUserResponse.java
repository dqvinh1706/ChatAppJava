package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@Getter
public class DeleteUserResponse extends Response {
    private String notification;
    @Builder
    public DeleteUserResponse(@NonNull StatusCode statusCode, String notification) {
        super(statusCode);
        this.notification = notification;
    }
}