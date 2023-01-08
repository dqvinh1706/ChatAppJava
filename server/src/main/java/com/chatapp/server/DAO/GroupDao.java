package com.chatapp.server.DAO;

import com.chatapp.commons.models.Group;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.models.User;
import com.chatapp.commons.utils.TimestampUtil;
import lombok.Synchronized;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GroupDao extends DAO<Group> {
    public static volatile GroupDao INSTANCE;

    @Synchronized
    public static GroupDao getInstance() {
        if (INSTANCE == null) INSTANCE = new GroupDao();
        return INSTANCE;
    }

    private GroupDao() {}

    @Override
    public List<Group> parse(ResultSet rs) {
        List<Group> result = new ArrayList<>();
        try{
            while (rs.next()) {
                result.add(new Group(rs));
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }

        return result.isEmpty() ? null : result;
    }

    public List<Group> getAllGroups() {
        return this.executeQuery("SELECT * FROM [group]");
    }
}
