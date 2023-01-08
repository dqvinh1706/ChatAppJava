package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Group;
import com.chatapp.commons.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class AllGroupsResponse extends Response {
    private final List<Group> allGroups;

    @Builder
    public AllGroupsResponse(@NonNull StatusCode statusCode, List<Group> allGroups) {
        super(statusCode);
        this.allGroups = allGroups;
    }
}