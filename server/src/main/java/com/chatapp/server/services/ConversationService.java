package com.chatapp.server.services;

import com.chatapp.commons.models.User;
import com.chatapp.server.DAO.ConversationDao;
import com.chatapp.commons.models.Conversation;
import lombok.Synchronized;

import java.util.List;

public class ConversationService {
    private final ConversationDao conversationDao = ConversationDao.getInstance();
    public static volatile ConversationService INSTANCE;
    private ConversationService() {}

    public static ConversationService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new ConversationService();
        }
        return INSTANCE;
    }

    public List<Conversation> getALlGroup(){return conversationDao.getALlGroup();}
    public Conversation getConversationById(int conversationId) {
        return conversationDao.getConversationById(conversationId);
    }
    public Conversation getOneOneConversation(int userId1, int userId2) {
        return conversationDao.getOneOneConversation(userId1, userId2);
    }

    public List<Conversation> getAllConversationOfUser(int userId) {
        return conversationDao.getAllConversationOfUser(userId);
    }

    public int saveConversation(Conversation con, List<Integer> usersId) {
        return conversationDao.saveConversation(con, usersId);
    }

    public int saveAdmin(int conId, int userId) {
        return conversationDao.saveAdmin(conId, userId);
    }

    public int deleteMember(int conId, int userId) {
        return conversationDao.deleteMember(conId, userId);
    }

    public int updateTitle(int conId, String newTitle) {
        return conversationDao.updateTitle(conId, newTitle);
    }

    public int updateCreator(int conId, int userId) {
        return conversationDao.updateCreator(conId, userId);
    }

    public boolean deleteConversation(int conId, int userId){
        return conversationDao.deleteConversation(conId, userId);
    }

    public int addMember(int conId, int userId) {
        return conversationDao.addMember(conId, userId, "group");
    }
}
