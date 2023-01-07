package com.chatapp.server.services;

import com.chatapp.commons.models.LoginHistory;
import com.chatapp.server.DAO.LoginHistoryDao;
import lombok.Synchronized;

import java.util.List;

public class LoginHistoryService {
    private static volatile LoginHistoryService INSTANCE;
    private final LoginHistoryDao loginHistoryDao = LoginHistoryDao.getInstance();

    private LoginHistoryService() {}

    @Synchronized
    public static LoginHistoryService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new LoginHistoryService();
        }
        return INSTANCE;
    }

    @Synchronized
    public List<LoginHistory> getAllLoginHistories() {
        return loginHistoryDao.getAllLoginHistories();
    }
    @Synchronized
    public List<LoginHistory> getLoginList() {
        return loginHistoryDao.getLoginList();
    }

    @Synchronized
    public List<LoginHistory> getLoginHistory(int id) {
        return loginHistoryDao.getLoginHistory(id);
    }
}
