package me.vasujain.ecommerce_jdbc_template.service;


import me.vasujain.ecommerce_jdbc_template.dto.ProductDTO;
import me.vasujain.ecommerce_jdbc_template.model.Product;

public interface ProductService {
    Object getProducts(boolean paginate, int page, int size);
    Product getProduct(Long id);
    Product createProduct(ProductDTO dto);
    Product updateProduct(Long id, ProductDTO dto);
    void deleteProduct(Long id);
}