module com.chatapp.commons {
    requires static lombok;
    requires java.sql;
    requires jbcrypt;

    opens com.chatapp.commons.models to com.google.gson;
    exports com.chatapp.commons.enums;
    exports com.chatapp.commons.request;
    exports com.chatapp.commons.response;
    exports com.chatapp.commons.models;
    exports com.chatapp.commons.utils;
}