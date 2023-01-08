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
public class Conversation implements Serializable {
    private int id;
    private String title;
    private int creatorId;
    @Builder.Default
    private Boolean isGroup = false;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Conversation() {
        setCreatedAt(TimestampUtil.getCurrentTime());
        setUpdatedAt(getCreatedAt());
    }

    @Builder
    public Conversation(String title, int userId) {
        this();
        setTitle(title);
        setCreatorId(userId);
    }

    public Conversation(ResultSet rs) throws SQLException {
        setId(rs.getInt("id"));
        setTitle(rs.getString("title"));
        setCreatorId(rs.getInt("creator_id"));
        setIsGroup(rs.getBoolean("is_group"));
        setCreatedAt(rs.getTimestamp("created_at"));
        setUpdatedAt(rs.getTimestamp("updated_at"));
    }
}
