module com.chatapp.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires net.synedra.validatorfx;
    requires static lombok;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.chatapp.commons;
    requires org.apache.commons.lang3;
    requires java.sql;

    opens com.chatapp.client to javafx.fxml;
    opens com.chatapp.client.controllers to javafx.fxml;
    opens com.chatapp.client.components.Avatar to javafx.fxml;
    opens com.chatapp.client.components.ConversationBox to javafx.fxml;
    opens com.chatapp.client.components.FriendBox to javafx.fxml;
    opens com.chatapp.client.components.CustomPasswordField to javafx.fxml;
    opens com.chatapp.client.components.CustomTextField to javafx.fxml;
    opens com.chatapp.client.components.MessageContainer to javafx.fxml;
    opens com.chatapp.client.components.UserTabs to javafx.fxml;

    exports com.chatapp.client;
    exports com.chatapp.client.controllers;
}
