package com.catadoption.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long catId;
    private Long userId;
    private String content;
    private Long parentId;
    private LocalDateTime createTime;
    
    // 扩展字段
    private String username;
    private String nickname;
    private String avatar;
}