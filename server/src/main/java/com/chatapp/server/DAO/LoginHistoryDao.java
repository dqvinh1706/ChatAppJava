package com.chatapp.server.DAO;

import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.utils.TimestampUtil;
import lombok.Synchronized;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginHistoryDao extends DAO<LoginHistory> {
    public static volatile LoginHistoryDao INSTANCE;

    @Synchronized
    public static LoginHistoryDao getInstance() {
        if (INSTANCE == null) INSTANCE = new LoginHistoryDao();
        return INSTANCE;
    }

    private LoginHistoryDao() {}

    @Override
    public List<LoginHistory> parse(ResultSet rs) {
        List<LoginHistory> result = new ArrayList<>();
        try{
            while (rs.next()) {
                result.add(new LoginHistory(rs));
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }

        return result.isEmpty() ? null : result;
    }

    public List<LoginHistory> getAllLoginHistories() {
        return this.executeQuery("SELECT * FROM [LoginHistory]");
    }

    public List<LoginHistory> getLoginList() {
        return this.executeQuery("SELECT U.id, U.username, U.full_name, LH.created_at  FROM [login_history] LH, [user] U " +
                "WHERE lh.user_id = u.id");
    }

    public List<LoginHistory> getLoginHistory(int id) {
        String sql = "SELECT U.id, U.username, U.full_name, LH.created_at  FROM [login_history] LH, [user] U " +
                "WHERE lh.user_id = u.id AND U.id = ? ORDER BY created_at DESC";

        return this.executeQuery(
                sql, id
        );
    }
}
