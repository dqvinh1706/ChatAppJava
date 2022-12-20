package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.Message;
import lombok.*;

@Getter
@Setter
public class MessageResponse extends Response {
    private Message message;

    @Builder
    public MessageResponse(@NonNull StatusCode statusCode, @NonNull Message message) {
        super(statusCode);
        this.message = message;
    }
}
