package com.chatapp.server.services;

import com.chatapp.server.DAO.UserDao;
import com.chatapp.commons.models.User;
import lombok.Synchronized;

import java.util.List;

public class UserService {
    private static volatile UserService INSTANCE;
    private final UserDao userDao = UserDao.getInstance();

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
    public boolean saveUser(User user) {
        return false;
    }

    @Synchronized
    public boolean deleteUserByUsername(String username) {
        return true;
    }

    @Synchronized
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Synchronized
    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public List<User> getAllFriends(int userId) {
        return userDao.getFriendsOfUser(userId);
    }

    public List<User> getAdminFromGroup(int conId) {
        return userDao.getAdminFromGroup(conId);
    }

    public List<User> getPendingFriends(int userId) {
        return userDao.getPendingFriends(userId);
    }
    public int addToPendingFriend(int userId, int friendId) {
        return userDao.addToPendingFriend(userId, friendId);
    }

    public List<User> getUsersInConversation(int conversationId) {
            return userDao.getUsersInConversation(conversationId);
    }

    public int acceptFriendRequest(int userId, int friendId) {
        userDao.saveFriend(userId, friendId);
        return userDao.removeFriendRequest(userId, friendId);
    }

    public int cancelFriendRequest(int userId, int friendId) {
        return userDao.removeFriendRequest(userId, friendId);
    }

    public void unfriend(int userId, int friendId) {
        userDao.unfriend(userId, friendId);
    }

    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }
}
