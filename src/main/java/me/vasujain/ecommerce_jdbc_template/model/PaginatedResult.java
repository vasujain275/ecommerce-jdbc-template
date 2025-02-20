package me.vasujain.ecommerce_jdbc_template.model;

import lombok.Value;
import java.util.List;

@Value
public class PaginatedResult<T> {
    List<T> content;
    int totalElements;
    int currentPage;
    int pageSize;
}