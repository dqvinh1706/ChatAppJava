package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Properties;

@Getter
public class SearchRequest extends Request {
    private Properties params;
    private Class _class;

    @Builder
    public SearchRequest(@NonNull Action action, Properties params, Class _class) {
        super(action);
        this.params = params;
        this._class = _class;
    }
}
