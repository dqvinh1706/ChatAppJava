package com.chatapp;

import com.chatapp.models.User;
import com.chatapp.services.UserService;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Synchronized;

public class StoreContext {
    private static volatile StoreContext INSTANCE;

    @Getter
    private SimpleObjectProperty<User> mainUser = new SimpleObjectProperty<>();

    private StoreContext() {}

    @Synchronized
    public static StoreContext getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new StoreContext();
        }
        return INSTANCE;
    }
}
