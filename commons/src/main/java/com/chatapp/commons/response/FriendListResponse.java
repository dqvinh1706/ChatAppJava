package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class FriendListResponse extends Response {
    private String title;
    private List<User> friendList;

    @Builder
    public FriendListResponse(@NonNull StatusCode statusCode,String title, List<User> friendList) {
        super(statusCode);
        this.title = title;
        this.friendList = friendList;
    }
}
