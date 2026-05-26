package com.catadoption.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Carousel {
    private Long id;
    private Long catId;
    private String imageUrl;
    private String title;
    private String linkUrl;
    private Integer sortOrder;
    private String status;
    private LocalDateTime createTime;
}