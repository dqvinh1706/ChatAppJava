package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@Getter
public class ActionResponse extends Response {
    private final String notification;

    @Builder
    public ActionResponse(@NonNull StatusCode statusCode, String notification) {
        super(statusCode);
        this.notification = notification;
    }
}