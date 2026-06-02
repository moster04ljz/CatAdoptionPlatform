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
    private String imageUrls;  // JSON数组，多图URL列表，最多8张
    private String description;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
