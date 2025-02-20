package me.vasujain.ecommerce_jdbc_template.service;

import me.vasujain.ecommerce_jdbc_template.exception.ResourceNotFoundException;
import me.vasujain.ecommerce_jdbc_template.model.Product;
import me.vasujain.ecommerce_jdbc_template.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        logger.info("Fetching all products");
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        logger.info("Fetching product with id={}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id - " + id));
    }

    public Product createProduct(Product product) {
        logger.info("Creating a new product");
        // Ensure id is null to allow auto-generation
        product.setId(null);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        logger.info("Updating product with id={}", id);
        Product existingProduct = getProduct(id);
        // Update only provided fields
        if (product.getName() != null) existingProduct.setName(product.getName());
        if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
        if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
        if (product.getStockQuantity() != null) existingProduct.setStockQuantity(product.getStockQuantity());
        return productRepository.update(existingProduct);
    }

    public void deleteProduct(Long id) {
        logger.info("Deleting product with id={}", id);
        // Verify product exists before attempting deletion
        getProduct(id);
        productRepository.deleteById(id);
    }
}
