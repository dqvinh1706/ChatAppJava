package com.chatapp.services;

import com.chatapp.DAO.DAO;
import com.chatapp.models.User;
import lombok.Synchronized;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private static volatile UserService INSTANCE;

    private UserService() {}

    @Synchronized
    public static UserService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
    }

    @Synchronized
    public List<User> getAllUser() {
        return null;
    }

    @Synchronized
    public User getUserByUsername(String username) {
        return null;
    }

    @Synchronized
    public boolean saveUser(User user) {
        return false;
    }

    @Synchronized
    public boolean deleteUserByUsername(String username) {
        return true;
    }



}
