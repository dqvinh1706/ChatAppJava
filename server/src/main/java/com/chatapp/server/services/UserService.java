package com.chatapp.server.services;

import com.chatapp.commons.models.Group;
import com.chatapp.commons.models.LoginHistory;
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
    public boolean saveUser(User user) {
        return false;
    }

    @Synchronized
    public boolean deleteUserById(int id) {
        if(userDao.deleteLoginHistory(id))
            return userDao.deleteUser(id);
        else return false;
    }

    public boolean lockUser(int id) {return userDao.lockUser(id); }

    @Synchronized
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Synchronized
    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public List<User> getFriendByID(int userID){
        return userDao.getFriendById(userID);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public List<Group> getAllGroups(){return userDao.getAllGroups();}

    public Boolean addNewUser(User newUser){return userDao.addNewUser(newUser);}

    public List<User> getAllFriends(int userId) {
        return userDao.getFriendsOfUser(userId);
    }

    public List<User> getPendingFriends(int userId) {
        return userDao.getPendingFriends(userId);
    }

    public List<User> getUsersInConversation(int conversationId) {
        return userDao.getUsersInConversation(conversationId);
    }

    public Boolean changePassword(User userAndPassword){
        return userDao.changePassword(userAndPassword);
    }
    public List<User> getAdminByGroupID(int GroupID){ return userDao.getAdminByGroupID(GroupID);}
    public int acceptFriendRequest(int userId, int friendId) {
        userDao.saveFriend(userId, friendId);
        return userDao.removeFriendRequest(userId, friendId);
    }
    public boolean setLogin(User user){
        return userDao.setLogin(user);
    }

    public int cancelFriendRequest(int userId, int friendId) {
        return userDao.removeFriendRequest(userId, friendId);
    }
}
