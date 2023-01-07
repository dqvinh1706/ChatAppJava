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
public class LoginListRespone extends Response {
    private List<LoginHistory> loginList;

    @Builder
    public LoginListRespone(@NonNull StatusCode statusCode, List<LoginHistory> loginList) {
        super(statusCode);
        this.loginList = loginList;
    }
}