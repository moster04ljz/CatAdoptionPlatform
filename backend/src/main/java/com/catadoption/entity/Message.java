package com.catadoption.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long catId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;

    // 扩展字段
    private String senderName;
    private String senderAvatar;
    private String receiverName;
    private String receiverAvatar;
    private String catName;
    private String catImage;
}
