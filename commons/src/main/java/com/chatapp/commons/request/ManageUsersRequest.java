package com.chatapp.commons.request;

import com.chatapp.commons.enums.Action;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ManageUsersRequest extends Request{
    private Object body;
    @Builder
    public ManageUsersRequest(@NonNull Action action, Object body){
        super(action);
        this.body = body;
    }
}
