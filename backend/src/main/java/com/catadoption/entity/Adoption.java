package com.catadoption.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Adoption {
    private Long id;
    private Long catId;
    private Long userId;
    private String status; // pending/approved/rejected
    
    // 领养申请材料
    private String realName;
    private String phone;
    private String occupation;
    private String housing;
    private String experience;
    private String reason;
    
    private LocalDateTime applyTime;
    private LocalDateTime updateTime;
    
    // 扩展字段
    private String catName;
    private String userName;
}