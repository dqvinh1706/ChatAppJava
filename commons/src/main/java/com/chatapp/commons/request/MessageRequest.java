package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.Message;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MessageRequest extends Request {
    private Object body;

    @Builder
    public MessageRequest(@NonNull Action action, Object body) {
        super(action);
        this.body = body;
    }
}
