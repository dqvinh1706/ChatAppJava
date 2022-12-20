package com.chatapp.server.DAO;

import com.chatapp.commons.models.Conversation;
import com.chatapp.commons.models.User;
import com.chatapp.commons.utils.TimestampUtil;
import lombok.Synchronized;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConversationDao extends DAO<Conversation>{
    public static volatile ConversationDao INSTANCE;

    @Synchronized
    public static ConversationDao getInstance() {
        if (INSTANCE == null) INSTANCE = new ConversationDao();
        return INSTANCE;
    }

    @Override
    public List<Conversation> parse(ResultSet rs) {
        List<Conversation> result = new ArrayList<>();
        try{
            while (rs.next()) {
                result.add(new Conversation(rs));
            }
        } catch (SQLException err) {
            return null;
        }

        return result.isEmpty() ? null : result;
    }

    public Conversation getConversationById(int conversationId) {
        List<Conversation> result = this.executeQuery("SELECT * FROM [conversation] WHERE id = ?", conversationId);
        return result == null ? null : result.get(0);
    }

    public Conversation getOneOneConversation(int userId1, int userId2) {
        String sql = "SELECT * " +
                    "FROM conversation " +
                    "WHERE id = " +
                        "(SELECT conversation_id " +
                            "FROM [participant] " +
                            "WHERE users_id = ? AND type='single' " +
                            "INTERSECT " +
                            "SELECT conversation_id " +
                            "FROM [participant] " +
                            "WHERE users_id = ? AND type='single')";
        List<Conversation> result = this.executeQuery(
                sql,
                userId1,
                userId2);
        return result == null ? null : result.get(0);
    }

    public List<Conversation> getAllConversationOfUser(int userId) {
        String sql = "SELECT * " +
                     "FROM [conversation] C JOIN [participant] P ON C.id = P.conversation_id " +
                     "WHERE P.users_id = ? AND c.id NOT IN " +
                     "(SELECT id FROM " +
                     "(SELECT D.conversation_id as id, MAX(D.created_at) AS deleted_time, MAX(m.created_at) AS last_updated " +
                     "FROM [deleted_conversations] D JOIN [message] m ON m.conversation_id = D.conversation_id " +
                     "WHERE D.users_id = ? " +
                     "GROUP BY D.conversation_id) AS T " +
                     "WHERE T.last_updated < T.deleted_time) " +
                     "ORDER BY c.updated_at DESC";
        return this.executeQuery(sql, userId,userId);
    }

    public int saveConversation(Conversation con, List<Integer> usersId) {
        String sql = "INSERT INTO [conversation] (title, creator_id, created_at, updated_at) VALUES (?, ?, ?, ?)";
        Long newConId = this.executeUpdate(sql, con.getTitle(), con.getCreatorId(), con.getCreatedAt(), con.getUpdatedAt());

        String sqlAddUsers = "INSERT INTO [participant](conversation_id, users_id, created_at, updated_at, type) VALUES (?, ?, ?, ?, ?)";
        usersId.forEach(user -> {
            this.executeUpdate(
                    sqlAddUsers,
                    newConId.intValue(),
                    user,
                    con.getCreatedAt(),
                    con.getCreatedAt(),
                    usersId.size() == 2 ? "single" : "group"
            );
        });
        return newConId.intValue();
    }

    public boolean deleteConversation(int conId, int userId) {
        String sqlDelete = "DELETE FROM [deleted_conversations]" +
                "WHERE conversation_id = ? AND users_id = ? " +
                "AND EXISTS(SELECT TOP 1 * FROM [deleted_conversations] WHERE conversation_id = ? AND users_id = ?)";

        Timestamp currTime = TimestampUtil.getCurrentTime();
        String sql = "INSERT INTO [deleted_conversations](conversation_id, users_id, created_at) VALUES (?, ?, ?)";
        try{
            this.executeUpdate(sqlDelete, conId, userId, conId, userId);
            this.executeUpdate(sql, conId, userId, currTime);
        }catch (Exception err) {
            err.printStackTrace();
            return false;
        }
        return true;
    }
}
