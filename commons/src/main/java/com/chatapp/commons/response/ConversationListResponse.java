package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.models.Message;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Getter
public class ConversationListResponse extends Response {
    private Map<Conversation, Message> conversations;

    @Builder
    public ConversationListResponse(@NonNull StatusCode statusCode, Map<Conversation, Message> conversations) {
        super(statusCode);
        this.conversations = conversations;
    }
}
