package com.catadoption.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Cat {
    private Long id;
    private String name;
    private String breed;
    private Integer age;
    private String gender;
    private String color;
    private String description;
    private String imageUrl;
    private String status; // available/adopted/pending_review
    private String category; // adoption/market (领养/商城)
    private Long addUserId;
    private Boolean isHot;
    private String healthStatus;
    private String vaccineRecord;
    private Boolean isSterilized;
    private BigDecimal price;
    private String location;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}