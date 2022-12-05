module com.chatapp.chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.microsoft.sqlserver.jdbc;
    requires java.sql;
    requires static lombok;

    opens com.chatapp to javafx.fxml;
    opens com.chatapp.controllers to javafx.fxml;
    opens com.chatapp.components.Avatar to javafx.fxml;
    opens com.chatapp.components.CustomPasswordField to javafx.fxml;
    opens com.chatapp.components.CustomTextField to javafx.fxml;
    opens com.chatapp.components.ConversationBox to javafx.fxml;
    exports com.chatapp;
}