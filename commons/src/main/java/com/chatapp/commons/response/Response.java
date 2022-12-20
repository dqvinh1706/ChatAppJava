package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import lombok.*;

import java.io.Serializable;
import java.util.Properties;

@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class Response implements Serializable {
    @NonNull
    protected StatusCode statusCode;
}
