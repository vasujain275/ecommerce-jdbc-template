package me.vasujain.ecommerce_jdbc_template.service;

import me.vasujain.ecommerce_jdbc_template.dto.ProductDTO;
import me.vasujain.ecommerce_jdbc_template.exception.ResourceNotFoundException;
import me.vasujain.ecommerce_jdbc_template.model.Product;
import me.vasujain.ecommerce_jdbc_template.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getProducts(boolean paginate, int page, int size) {
        logger.info("Fetching products with pagination={}", paginate);
        if (paginate) {
            return productRepository.findAllPaginated(page, size);
        } else {
            return productRepository.findAll();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        logger.info("Fetching product with id={}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id - " + id));
    }

    @Override
    public Product createProduct(ProductDTO dto) {
        logger.info("Creating a new product");
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .build();
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, ProductDTO dto) {
        logger.info("Updating product with id={}", id);
        Product product = getProduct(id);

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getStockQuantity() != null) product.setStockQuantity(dto.getStockQuantity());

        return productRepository.update(product);
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("Deleting product with id={}", id);
        getProduct(id); // Verify product exists
        productRepository.deleteById(id);
    }
}