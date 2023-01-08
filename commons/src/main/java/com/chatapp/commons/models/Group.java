package com.chatapp.commons.models;

import com.chatapp.commons.utils.TimestampUtil;
import lombok.*;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public final class Group implements Serializable {
    private int id;
    private String name;
    private int AdminID;
    @Builder.Default
    private Timestamp createdAt = TimestampUtil.getCurrentTime();
    @Builder.Default
    private Timestamp updatedAt = TimestampUtil.getCurrentTime();


    public Group() {
        setCreatedAt(TimestampUtil.getCurrentTime());
        setUpdatedAt(getUpdatedAt());
    }

    @Builder
    public Group(int id, String name, int AdminID) {
        setAdminID(AdminID);
        setId(id);
        setName(name);
    }
    public Group getGroup(){ return this; }

    public Group(ResultSet rs) {
        try{
            setId(rs.getInt("id"));
            setName(rs.getString("name"));
            setAdminID(rs.getInt("admin"));
            setCreatedAt(rs.getTimestamp("created_at"));
            setUpdatedAt(rs.getTimestamp("updated_at"));
        }
        catch (SQLException err) {}
    }
}
