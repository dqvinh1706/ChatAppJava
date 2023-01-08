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
public class GroupMemberResponse extends Response {
    private List<User> GroupMembers;

    @Builder
    public GroupMemberResponse(@NonNull StatusCode statusCode, List<User> groupMembers) {
        super(statusCode);
        this.GroupMembers = groupMembers;
    }
}