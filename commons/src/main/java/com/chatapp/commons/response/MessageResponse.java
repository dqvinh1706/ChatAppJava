package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Message;
import lombok.*;

@Getter
@Setter
public class MessageResponse extends Response {
    private Message message;
    private String senderName;
    @Builder
    public MessageResponse(@NonNull StatusCode statusCode, @NonNull Message message, String senderName) {
        super(statusCode);
        this.message = message;
        this.senderName = senderName;
    }
}
