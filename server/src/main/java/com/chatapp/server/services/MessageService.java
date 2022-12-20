package com.chatapp.server.services;

import com.chatapp.server.DAO.MessageDao;
import com.chatapp.commons.models.Message;
import lombok.Synchronized;

import java.util.List;

public class MessageService {
    private final MessageDao messageDao = MessageDao.getInstance();
    private static volatile MessageService INSTANCE;

    private MessageService() {}

    @Synchronized
    public static MessageService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new MessageService();
        }
        return INSTANCE;
    }

    public int saveMessage(Message message) throws Exception {
        return messageDao.saveMessage(message).intValue();
    }

    public List<Message> getMessageFromConversation(int conversationId,int userId, int offset, int limit) {
        return messageDao.getMessagesFromConversation(conversationId, userId, offset, limit);
    }

    public Message getNewestMessage(int conversationId) {
        return messageDao.getNewestMessage(conversationId);
    }
}
