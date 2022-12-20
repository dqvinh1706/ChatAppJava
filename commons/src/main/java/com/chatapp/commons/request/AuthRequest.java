package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Properties;

@Getter
@ToString
public class AuthRequest extends Request {
    private Properties formData;

    @Builder
    public AuthRequest(@NonNull Action action, Properties formData) {
        super(action);
        this.formData = formData;
    }
}
