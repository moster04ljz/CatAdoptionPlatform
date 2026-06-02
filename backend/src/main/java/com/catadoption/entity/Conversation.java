package com.catadoption.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Conversation {
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private Long catId;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String senderName;
    private String senderAvatar;
    private String receiverName;
    private String receiverAvatar;
    private String catName;
    private String catImage;
}
