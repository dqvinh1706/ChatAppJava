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
    ADD_PENDING_FRIEND,
    UNFRIEND,


    // -> CRUD Message
    GET_CHAT_HISTORY,
    SEND_MESSAGE,

    // Group chat
    CREATE_GROUP_CHAT,
    SEARCH_USER,
    CHANGE_TITLE,
    GET_MEMBERS,
    SEARCH_FRIEND,
    ADD_MEMBER,
    CHANGE_ADMIN,
    KICK_MEMBER,

    // Admin action
}
