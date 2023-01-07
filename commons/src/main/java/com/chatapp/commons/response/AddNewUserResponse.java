package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@Getter
public class AddNewUserResponse extends Response {
    @Builder
    public AddNewUserResponse(@NonNull StatusCode statusCode) {
        super(statusCode);
    }
}