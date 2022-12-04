package com.chatapp.models;

import lombok.*;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

@Setter
@Getter
@NoArgsConstructor
@ToString
public final class User {
    private String username;
    private String password;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private int id;
    private String fullName;
    private String address;
    private boolean isActive;
    private boolean isBlocked;
    private Blob avatar;
    private Date DOB;
    private Boolean isAdmin;

    @Builder
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(ResultSet rs) throws SQLException {
        setId(rs.getInt("id"));
        setUsername(rs.getString("username"));
        setPassword(rs.getString("password"));
        setEmail(rs.getString("email"));
        setFullName(rs.getString("full_name"));
        setAvatar(rs.getBlob("avatar"));
        setActive(rs.getBoolean("is_active"));
        setBlocked(rs.getBoolean("is_blocked"));
        setAddress(rs.getString("address"));
        setDOB(rs.getDate("DOB"));
        setCreatedAt(rs.getDate("created_at"));
        setUpdatedAt(rs.getDate("updated_at"));
        setIsAdmin(rs.getBoolean("is_admin"));
    }
}
