package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Conversation;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ConversationResponse extends Response {
    private Conversation conversation;

    @Builder
    public ConversationResponse(@NonNull StatusCode statusCode, Conversation conversation) {
        super(statusCode);
        this.conversation = conversation;
    }
}
