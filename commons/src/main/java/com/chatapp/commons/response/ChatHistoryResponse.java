package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Message;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ChatHistoryResponse extends Response {
    private List<Message> messageList;

    @Builder
    public ChatHistoryResponse(@NonNull StatusCode statusCode, List<Message> messageList) {
        super(statusCode);
        this.messageList = messageList;
    }
}
