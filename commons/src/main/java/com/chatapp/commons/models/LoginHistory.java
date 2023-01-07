package com.chatapp.commons.models;

import com.chatapp.commons.utils.TimestampUtil;
import lombok.*;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public class LoginHistory implements Serializable {
    private int id;
    private String username;
    @Builder.Default
    private Timestamp createdAt = TimestampUtil.getCurrentTime();

    public LoginHistory() {
        setCreatedAt(TimestampUtil.getCurrentTime());
    }

    @Builder
    public LoginHistory(int id, String username, String name) {
        setId(id);
        setUsername(username);
        setCreatedAt(TimestampUtil.getCurrentTime());
    }
    public LoginHistory getLoginHistory(){ return this; }

    public LoginHistory(ResultSet rs) {
        try{
            setId(rs.getInt("id"));
            setUsername(rs.getString("username"));
            setCreatedAt(rs.getTimestamp("created_at"));
        }
        catch (SQLException err) {}
    }
}
