package com.catadoption.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String description;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}