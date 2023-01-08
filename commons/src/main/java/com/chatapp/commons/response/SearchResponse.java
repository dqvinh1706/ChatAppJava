package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class SearchResponse extends Response {
    private Object result;

    @Builder
    public SearchResponse(@NonNull StatusCode statusCode, Object result) {
        super(statusCode);
        this.result = result;
    }
}
