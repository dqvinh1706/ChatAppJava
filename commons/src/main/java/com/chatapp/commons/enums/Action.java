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
    REMOVE_ADD_FRIEND,


    // -> CRUD Message
    GET_CHAT_HISTORY,
    SEND_MESSAGE,

    // Group chat
    CREATE_GROUP_CHAT, SEARCH_FRIEND, CHANGE_TITLE,

    // Admin action
    GET_ALL_USERS,
    ADD_NEW_USER,
    DELETE_USER,
    GET_LOGIN_LIST,
    CHANGE_PASSWORD,
    GET_PASSWORD_BY_ID,
    GET_FRIEND_BY_ID,
    LOCK_USER,
    SHOW_LOGIN_HISTORY,
    GET_ALL_GROUPS,
    GET_ADMIN_BY_GROUP_ID,
}
