module com.chatapp.server {
    requires javafx.fxml;
    requires javafx.controls;
    requires static lombok;
    requires org.apache.commons.lang3;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    requires com.google.gson;
    requires com.chatapp.client;
    requires com.chatapp.commons;

    opens com.chatapp.server to javafx.fxml;
    opens com.chatapp.server.handlers to javafx.fxml;

    exports com.chatapp.server;
    exports com.chatapp.server.handlers;
}