package com.chatapp.server.DAO;

import com.chatapp.commons.models.User;
import lombok.Synchronized;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDao extends DAO<User>{
    public static volatile AdminDao INSTANCE;

    @Synchronized
    public static AdminDao getInstance() {
        if (INSTANCE == null) INSTANCE = new AdminDao();
        return INSTANCE;
    }

    private AdminDao() {}

    @Override
    public List<User> parse(ResultSet rs) {
        List<User> result = new ArrayList<>();
        try{
            while (rs.next()) {
                result.add(new User(rs));
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
        return result.isEmpty() ? null : result;
    }
    public List<User> getAllUsers() {
        return this.executeQuery("SELECT * FROM [user]");
    }
}
