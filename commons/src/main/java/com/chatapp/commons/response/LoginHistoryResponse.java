package com.chatapp.commons.response;

import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.models.LoginHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class LoginHistoryResponse extends Response {
    private List<LoginHistory> loginHistories;

    @Builder
    public LoginHistoryResponse(@NonNull StatusCode statusCode, List<LoginHistory> loginHistories) {
        super(statusCode);
        this.loginHistories = loginHistories;
    }
}