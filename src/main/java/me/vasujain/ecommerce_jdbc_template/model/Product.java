package me.vasujain.ecommerce_jdbc_template.model;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isActive;
    private LocalDateTime createdAt;
}