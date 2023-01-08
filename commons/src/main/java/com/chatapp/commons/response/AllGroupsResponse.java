package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Conversation;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class AllGroupsResponse extends Response {
    private final List<Conversation> allGroups;

    @Builder
    public AllGroupsResponse(@NonNull StatusCode statusCode, List<Conversation> allGroups) {
        super(statusCode);
        this.allGroups = allGroups;
    }
}