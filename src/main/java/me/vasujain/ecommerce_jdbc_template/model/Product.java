package me.vasujain.ecommerce_jdbc_template.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
}
