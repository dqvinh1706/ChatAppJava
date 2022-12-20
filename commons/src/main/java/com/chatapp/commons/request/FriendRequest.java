package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
public class FriendRequest extends Request {
    private Object body;

    @Builder
    public FriendRequest(@NonNull Action action, Object body) {
        super(action);
        this.body = body;
    }
}
