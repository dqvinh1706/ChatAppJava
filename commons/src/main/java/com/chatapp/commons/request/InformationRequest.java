package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class InformationRequest extends Request {

    @Builder
    public InformationRequest(@NonNull Action action) {
        super(action);
    }
}
