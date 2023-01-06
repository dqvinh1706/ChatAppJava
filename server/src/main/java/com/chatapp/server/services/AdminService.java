package com.chatapp.server.services;

import com.chatapp.commons.models.User;
import com.chatapp.server.DAO.AdminDao;
import lombok.Synchronized;

import java.util.List;

public class AdminService {
    private static volatile AdminService INSTANCE;
    private final AdminDao adminDao = AdminDao.getInstance();

    @Synchronized
    public static AdminService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new AdminService();
        }
        return INSTANCE;
    }

    public List<User> getAllUsers() {
        return adminDao.getAllUsers();
    }

}
