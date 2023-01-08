package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class SearchResponse extends Response {
    private Object result;
    private Class _class;

    @Builder
    public SearchResponse(@NonNull StatusCode statusCode, Object result, Class _class) {
        super(statusCode);
        this.result = result;
        this._class = _class;
    }
}
