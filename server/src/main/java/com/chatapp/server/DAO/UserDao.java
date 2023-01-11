package com.chatapp.server.DAO;

import com.chatapp.commons.models.Group;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.models.User;
import com.chatapp.commons.utils.PasswordUtil;
import com.chatapp.commons.utils.TimestampUtil;
import lombok.Synchronized;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Arrays;
import java.util.List;

public class UserDao extends DAO<User> {
    public static volatile UserDao INSTANCE;

    @Synchronized
    public static UserDao getInstance() {
        if (INSTANCE == null) INSTANCE = new UserDao();
        return INSTANCE;
    }

    private UserDao() {}

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

    public User getUserById(int userId) {
        List<User> result = this.executeQuery("SELECT * FROM [user] WHERE id = ?", userId);
        return result == null ? null : result.get(0);
    }

    public List<User> getFriendById(int userId){
        return this.executeQuery("SELECT u.*\n" +
                "FROM friends_list fl join [user] u on u.id = fl.friend_id\n" +
                "WHERE fl.user_id = ?", userId);
    }

    public List<User> getAdminByGroupID(int GroupID){
        return this.executeQuery("select *\n" +
                "from [user] join admin_list al on [user].id = al.admin_id\n" +
                "where al.conversation_id = ?", GroupID);
    }

    public boolean setLogin(User user){
        long result = this.executeUpdate("insert into login_history(user_id, created_at)\n" +
                "values (?, ?)", user.getId(), TimestampUtil.getCurrentTime());
        return result != -1;
    }

    public User getUserByUsername(String username) {
        List<User> result = this.executeQuery("SELECT * FROM [user] WHERE username = ?", username);
        return result == null ? null : result.get(0);
    }

    public List<User> getAllUsers() {
        return this.executeQuery("SELECT * FROM [user]");
    }

    public boolean signUp(String username, String password, String email) {
        String sql = "INSERT INTO [user](username, password, email, created_at, updated_at, DOB) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Calendar cal = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        long result = this.executeUpdate(
                sql,
                username,
                PasswordUtil.encode(password),
                email,
                timestamp,
                timestamp,
                timestamp
        );

        return result != -1;
    }

    public boolean addNewUser(User user) {
        String sql = "INSERT INTO [user](username, password, full_name, address, email, gender, DOB, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        long result = this.executeUpdate(
                                        sql,
                                        user.getUsername(),
                                        user.getPassword(),
                                        user.getFullName(),
                                        user.getAddress(),
                                        user.getEmail(),
                                        user.getGender(),
                                        user.getDOB(),
                                        user.getCreatedAt(),
                                        user.getUpdatedAt()
                                    );

        return result != -1;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE from [user] where id = ?";
        long result = this.executeUpdate(
                sql, id
        );

        return result != -1;
    }

    public boolean lockUser(int id) {
        String sql = "UPDATE [user] SET is_blocked = 1 WHERE id = ?";
        long result = this.executeUpdate(
                sql, id
        );

        return result != -1;
    }

    public List<User> getFriendsOfUser(int userId) {
        List<User> result = null;
        String sql = "SELECT U.* " +
                "FROM (SELECT friend_id as id FROM [friends_list] WHERE user_id = ?) AS T JOIN [user] U ON T.id = U.id";
        result = this.executeQuery(sql, userId);
        return result;
    }

    public List<User> getPendingFriends(int userId) {
        String sql = "SELECT U.* " +
                "FROM (SELECT creator_id as id FROM [pending_add_friend] WHERE user_id = ?) AS T JOIN [user] U ON T.id = U.id";
        return this.executeQuery(sql, userId);
    }

    public List<User> getUsersInConversation(int conversationId) {
        String sql = "SELECT * " +
                "FROM [user] " +
                "WHERE id in " +
                "(SELECT P.users_id " +
                "FROM [participant] P JOIN [conversation] c on P.conversation_id = c.id " +
                "WHERE c.id = ?)";

        return this.executeQuery(sql, conversationId);
    }
    public int addToPendingFriend(int userId, int friendId) {
        String sql = "INSERT INTO [pending_add_friend](creator_id, user_id, created_at) VALUES(?, ?, ?)";
        return this.executeUpdate(sql, userId, friendId, TimestampUtil.getCurrentTime()).intValue();
    }
    public int saveFriend(int userId, int friendId) {
        String sql = "INSERT INTO [friends_list](user_id, friend_id) VALUES (?, ?)";
        this.executeUpdate(sql, userId, friendId);
        return this.executeUpdate(sql, friendId, userId).intValue();
    }

    public void unfriend(int userId, int friendId) {
        String sql = "DELETE FROM [friends_list]WHERE user_id = ? AND friend_id = ?";
        this.executeUpdate(sql, userId, friendId);
        this.executeUpdate(sql, friendId, userId);
    }

    public int removeFriendRequest(int userId, int friendId) {
        String sql = "DELETE FROM [pending_add_friend] WHERE user_id = ? AND creator_id = ?";
        return this.executeUpdate(sql, userId, friendId).intValue();
    }

    public boolean changePassword(User userAndPassword) {

        String sql = "UPDATE [user] " +
                "SET password = ? " +
                "WHERE id = ?";
        String rawPass = userAndPassword.getPassword();
        String newPass = PasswordUtil.encode(rawPass);
        Long result = this.executeUpdate(
                sql,
                newPass,
                userAndPassword.getId()
        );

        return result != -1;
    }

    public List<User> getAllMembers(int id) {
        String sql = "SELECT U.* FROM [conversation] C, [user] U, [participant] P " +
                "WHERE P.users_id = U.id and P.conversation_id = C.id and C.is_group = 1 and C.id = ?";

        return this.executeQuery(
                sql, id
        );
    }
    public boolean updateUser(User user) {
        String sql = "UPDATE [user] SET username=?, full_name=?, password=?, email=?, gender=?, address=?, DOB=?, is_blocked=?, is_active=?, is_admin=?, updated_at=?" +
                " WHERE id=?";
        int result = this.executeUpdate(
                sql,
                user.getUsername(),
                user.getFullName(),
                user.getPassword(),
                user.getEmail(),
                user.getGender(),
                user.getAddress(),
                user.getDOB(),
                user.getIsBlocked(),
                user.getIsActive(),
                user.getIsAdmin(),
                TimestampUtil.getCurrentTime(),
                user.getId()
        ).intValue();

        return result == -1 ? false : true;
    }

    public List<User> getAdminFromGroup(int conId) {
        String sql = "SELECT u.* FROM [admin_list] admin JOIN [user] u ON u.id = admin.admin_id " +
                        "WHERE conversation_id = ?";
        return this.executeQuery(sql, conId);
    }
}
