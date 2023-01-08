package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Properties;

@Getter
public class SearchRequest extends Request {
    private Properties params;

    @Builder
    public SearchRequest(@NonNull Action action, Properties params) {
        super(action);
        this.params = params;
    }
}
