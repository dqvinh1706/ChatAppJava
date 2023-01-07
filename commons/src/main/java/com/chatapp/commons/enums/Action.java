package com.chatapp.commons.enums;

public enum Action {
    // Basic action
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    DISCONNECT,

    // User action
    // -> CRUD Conversation
    CREATE_CONVERSATION,
    GET_ALL_CONVERSATION,
    GET_ONE_CONVERSATION,
    UPDATE_CONVERSATION,
    DELETE_CONVERSATION,

    // -> CRUD Friend
    ADD_FRIEND,
    GET_ALL_FRIENDS,
    GET_PENDING_ADD_FRIEND,


    // -> CRUD Message
    GET_CHAT_HISTORY,
    SEND_MESSAGE, REMOVE_ADD_FRIEND,

    // Admin action
    GET_ALL_USERS,
    ADD_NEW_USER,
}
