package me.vasujain.ecommerce_jdbc_template.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
}