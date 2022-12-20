package com.chatapp.server.DAO;

import com.chatapp.commons.models.Message;
import lombok.Synchronized;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDao extends DAO<Message>{
    public static volatile MessageDao INSTANCE;

    @Synchronized
    public static MessageDao getInstance() {
        if (INSTANCE == null) INSTANCE = new MessageDao();
        return INSTANCE;
    }

    @Override
    public List<Message> parse(ResultSet rs) {
        List<Message> result = new ArrayList<>();
        try{
            while (rs.next()) {
                result.add(new Message(rs));
            }
        } catch (SQLException err) {
            return null;
        }

        return result.isEmpty() ? null : result;
    }

    public Message getNewestMessage(int conversationId) {
        String sql = "SELECT TOP 1 * " +
                "FROM [message] " +
                "WHERE conversation_id = ? " +
                "ORDER BY created_at DESC";
        List<Message> result = this.executeQuery(sql, conversationId);
        return result == null ? null : result.get(0);
    }

    public List<Message> getMessagesFromConversation(int conversationId, int userId, int offset, int limit) {
        String sql = "SELECT M.* " +
                "FROM [message] M LEFT JOIN [deleted_conversations] D ON M.conversation_id = D.conversation_id " +
                "WHERE M.conversation_id = ? AND (D.created_at IS NULL OR (D.users_id = ? AND D.created_at < M.created_at)) " +
                "ORDER BY M.created_at DESC " +
                "OFFSET " + offset + " ROWS " +
                "FETCH NEXT " + limit + " ROWS ONLY";
        return this.executeQuery(sql, conversationId, userId);
    }

    public Long saveMessage(Message message) throws Exception {
        String insertMessage = "INSERT INTO [message] (conversation_id, sender_id, message, created_at) VALUES (?, ?, ?, ?)";
        String updateConversation = "UPDATE [conversation] SET updated_at = ? WHERE id = ?";
        if (this.executeUpdate(
                updateConversation,
                message.getCreatedAt(),
                message.getConversationId()) == null)
        {
            throw new Exception("Can't update conversation");
        }
        return this.executeUpdate(insertMessage,
                message.getConversationId(),
                message.getSenderId(),
                message.getMessage(),
                message.getCreatedAt()
            );
    }
}
