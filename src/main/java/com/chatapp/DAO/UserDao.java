package com.chatapp.DAO;

import com.chatapp.models.User;
import lombok.Synchronized;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            return null;
        }

        return result.isEmpty() ? null : result;
    }

    public List<User> getAllUsers() {
        return this.executeQuery("SELECT * FROM [user]");
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO [user](username, password, email, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?)";
        int result = this.executeUpdate(
                                        sql,
                                        user.getUsername(),
                                        user.getPassword(),
                                        user.getEmail(),
                                        user.getCreatedAt(),
                                        user.getUpdatedAt()
                                    );

        return result == -1 ? false : true;
    }

//    public boolean updateUser(User user) {
//        String sql = "UPDATE [user](username, password, email, created_at, updated_at) " +
//                "VALUES (?, ?, ?, ?, ?)";
//        int result = this.executeUpdate(
//                sql,
//                user.getUsername(),
//                user.getPassword(),
//                user.getEmail(),
//                user.getCreatedAt(),
//                user.getUpdatedAt()
//        );
//
//        return result == -1 ? false : true;
//    }
}
