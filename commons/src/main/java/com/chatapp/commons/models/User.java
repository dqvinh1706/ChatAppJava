package com.chatapp.commons.models;

//import com.chatapp.client.components.FriendBox.FriendBox;

import com.chatapp.commons.utils.TimestampUtil;
import lombok.*;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public final class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String gender ;
    private String email;
    @Builder.Default
    private Timestamp createdAt = TimestampUtil.getCurrentTime();
    @Builder.Default
    private Timestamp updatedAt = TimestampUtil.getCurrentTime();
    private String fullName;
    private String address;
    private Boolean isActive;
    private Boolean isBlocked;
    private Blob avatar;
    private Date DOB;
    private Boolean isAdmin;

    public User() {
        setCreatedAt(TimestampUtil.getCurrentTime());
        setUpdatedAt(getUpdatedAt());
    }

    @Builder
    public User(String username, String password, String email) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
    }
    public User(
            String username,
            String password,
            String name,
            String address,
            String email,
            String gender,
            Date dob,
            Timestamp createdDay,
            Timestamp updatedDay
    ){
        this.username = username;
        this.password = password;
        this.address = address;
        this.email = email;
        this.gender = gender;
        this.DOB = dob;
        this.createdAt = createdDay;
        this.updatedAt = updatedDay;
        this.fullName = name;
    }
    public User(int id, String password){
        this.id = id;
        this.password = password;
    }
    public User getUser(){ return this; }

    public User(ResultSet rs) {
        try{
            setId(rs.getInt("id"));
            setUsername(rs.getString("username"));
            setPassword(rs.getString("password"));
            setEmail(rs.getString("email"));
            setFullName(rs.getString("full_name") == null ? "" : rs.getString("full_name").trim());
            setIsActive(rs.getBoolean("is_active"));
            setIsBlocked(rs.getBoolean("is_blocked"));
            setGender(rs.getString("gender") == null ? "" : rs.getString("gender").trim());
            setAddress(rs.getString("address") == null ? "" : rs.getString("address").trim());
            setDOB(rs.getDate("DOB"));
            setCreatedAt(rs.getTimestamp("created_at"));
            setUpdatedAt(rs.getTimestamp("updated_at"));
            setIsAdmin(rs.getBoolean("is_admin"));
        }
        catch (SQLException err) {}
    }
}
