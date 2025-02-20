package me.vasujain.ecommerce_jdbc_template.controller;

import me.vasujain.ecommerce_jdbc_template.dto.ProductDTO;
import me.vasujain.ecommerce_jdbc_template.model.ApiResponse;
import me.vasujain.ecommerce_jdbc_template.model.PaginatedResult;
import me.vasujain.ecommerce_jdbc_template.model.Product;
import me.vasujain.ecommerce_jdbc_template.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(defaultValue = "false") boolean paginate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.debug("Fetching products with pagination={} page={} size={}", paginate, page, size);
        Object result = productService.getProducts(paginate, page, size);

        if (result instanceof PaginatedResult) {
            PaginatedResult<Product> paginatedResult = (PaginatedResult<Product>) result;
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .data(paginatedResult.getContent())
                    .timestamp(LocalDate.now())
                    .pagination(ApiResponse.PaginationMetadata.builder()
                            .totalElements(paginatedResult.getTotalElements())
                            .currentPage(paginatedResult.getCurrentPage())
                            .pageSize(paginatedResult.getPageSize())
                            .totalPages((int) Math.ceil(paginatedResult.getTotalElements() / (double) size))
                            .build()
                    )
                    .build()
            );
        } else {
            List<Product> products = (List<Product>) result;
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .data(products)
                    .timestamp(LocalDate.now())
                    .build()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        logger.debug("Fetching product with id={}", id);
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.<Product>builder()
                .status(HttpStatus.OK)
                .data(product)
                .timestamp(LocalDate.now())
                .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody ProductDTO dto) {
        logger.debug("Creating a new product");
        Product newProduct = productService.createProduct(dto);
        return ResponseEntity.ok(ApiResponse.<Product>builder()
                .status(HttpStatus.CREATED)
                .data(newProduct)
                .timestamp(LocalDate.now())
                .message("Product created successfully")
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        logger.debug("Updating product with id={}", id);
        Product updatedProduct = productService.updateProduct(id, dto);
        return ResponseEntity.ok(ApiResponse.<Product>builder()
                .status(HttpStatus.OK)
                .data(updatedProduct)
                .timestamp(LocalDate.now())
                .message("Product updated successfully")
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
        logger.debug("Deleting product with id={}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Product deleted successfully")
                .timestamp(LocalDate.now())
                .build()
        );
    }
}