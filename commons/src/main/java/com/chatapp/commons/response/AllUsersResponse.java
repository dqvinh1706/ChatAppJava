package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class AllUsersResponse extends Response {
    private List<User> allUsers;

    @Builder
    public AllUsersResponse(@NonNull StatusCode statusCode, List<User> allUsers) {
        super(statusCode);
        this.allUsers = allUsers;
    }
}