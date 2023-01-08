package com.chatapp.commons.response;

import com.chatapp.commons.enums.Action;
import com.chatapp.commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class InformationResponse extends Response {
    private Class _class;
    private Action forAction;
    private Object body;

    @Builder
    public InformationResponse(@NonNull StatusCode statusCode,
                               Object body,
                               Action forAction,
                               Class _class) {
        super(statusCode);
        this.body = body;
        this.forAction = forAction;
        this._class = _class;
    }
}
