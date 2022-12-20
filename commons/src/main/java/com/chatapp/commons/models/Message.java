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
public class Message implements Serializable {
    private int id;
    private int conversationId;
    private int senderId;
    private String message;
    private Timestamp createdAt;

    public Message() {
        setCreatedAt(TimestampUtil.getCurrentTime());
    }

    public Message(int conversationId, int senderId) {
        this();
        setConversationId(conversationId);
        setSenderId(senderId);
    }

    public Message(ResultSet rs) throws SQLException {
        setId(rs.getInt("id"));
        setConversationId(rs.getInt("conversation_id"));
        setSenderId(rs.getInt("sender_id"));
        setMessage(rs.getString("message"));
        setCreatedAt(rs.getTimestamp("created_at"));
    }
}
