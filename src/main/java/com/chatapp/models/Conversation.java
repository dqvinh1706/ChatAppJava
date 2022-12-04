package com.chatapp.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class Conversation {
    private int id;
    private String title;
    private int creatorId;
    private Date createdAt;
    private Date updatedAt;
    private Message lastMessage;

}
