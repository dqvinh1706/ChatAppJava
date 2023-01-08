package com.chatapp.server.services;

import com.chatapp.commons.models.Group;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.server.DAO.GroupDao;
import com.chatapp.server.DAO.UserDao;
import com.chatapp.commons.models.User;
import lombok.Synchronized;

import java.util.List;

public class GroupService {
    private static volatile GroupService INSTANCE;
    private final GroupDao groupDao = GroupDao.getInstance();

    private GroupService() {}

    @Synchronized
    public static GroupService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new GroupService();
        }
        return INSTANCE;
    }

    @Synchronized

    public List<Group> getAllGroups(){return groupDao.getAllGroups();}
}
