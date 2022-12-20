package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import lombok.*;

import java.io.Serializable;
import java.util.Properties;

@Setter
@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class Request implements Serializable {
    @NonNull
    protected Action action;
}
