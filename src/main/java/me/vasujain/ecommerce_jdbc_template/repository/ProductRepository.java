package me.vasujain.ecommerce_jdbc_template.repository;

import me.vasujain.ecommerce_jdbc_template.mapper.ProductRowMapper;
import me.vasujain.ecommerce_jdbc_template.model.PaginatedResult;
import me.vasujain.ecommerce_jdbc_template.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ProductRowMapper productRowMapper;
    private final Logger logger = LoggerFactory.getLogger(ProductRepository.class);


    // SQL queries as constants for maintainability
    private static final String SELECT_ALL = "SELECT * FROM products";
    private static final String SELECT_BY_ID = "SELECT * FROM products WHERE id = ?";
    private static final String INSERT_PRODUCT = """
            INSERT INTO products (name, description, price, stock_quantity, created_at, updated_at, is_active)
            VALUES (?, ?, ?, ?, ?, ?, true)
            """;
    private static final String UPDATE_PRODUCT = """
            UPDATE products
            SET name = ?, description = ?, price = ?, stock_quantity = ?,
                updated_at = ?
            WHERE id = ?
            """;
    private static final String SOFT_DELETE = "UPDATE products SET is_active = false WHERE id = ?";
    private static final String COUNT_PRODUCTS = "SELECT COUNT(*) FROM products";
    private static final String SELECT_PAGINATED = """
            SELECT * FROM products
            ORDER BY id
            LIMIT ? OFFSET ?
            """;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRowMapper = new ProductRowMapper();
    }

    public List<Product> findAll() {
        logger.debug("Fetching all products");
        return jdbcTemplate.query(SELECT_ALL, productRowMapper);
    }

    public PaginatedResult<Product> findAllPaginated(int page, int size) {
        logger.debug("Fetching paginated products - page: {}, size: {}", page, size);

        // Calculate offset for pagination
        int offset = page * size;

        // Get total count of active products
        Integer totalElements = jdbcTemplate.queryForObject(COUNT_PRODUCTS, Integer.class);
        if (totalElements == null) {
            totalElements = 0;
        }

        // Fetch paginated data
        List<Product> products = jdbcTemplate.query(
                SELECT_PAGINATED,
                productRowMapper,
                size, offset
        );

        return new PaginatedResult<>(
                products,
                totalElements,
                page,
                size
        );
    }

    public Optional<Product> findById(Long id) {
        logger.debug("Finding product by id: {}", id);
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(SELECT_BY_ID, productRowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Product save(Product product) {
        logger.debug("Saving new product: {}", product);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_PRODUCT,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQuantity());
            ps.setTimestamp(5, Timestamp.valueOf(now));
            ps.setTimestamp(6, Timestamp.valueOf(now));

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            product.setId(key.longValue());
        }
        product.setCreatedAt(now);

        return product;
    }

    @Transactional
    public Product update(Product product) {
        logger.debug("Updating product: {}", product);

        LocalDateTime now = LocalDateTime.now();

        int updatedRows = jdbcTemplate.update(
                UPDATE_PRODUCT,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                Timestamp.valueOf(now),
                product.getId()
        );

        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException(
                    String.format("No product found with id: %d", product.getId()), 1);
        }

        return product;
    }

    @Transactional
    public void deleteById(Long id) {
        logger.debug("Soft deleting product with id: {}", id);

        int updatedRows = jdbcTemplate.update(SOFT_DELETE, id);

        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException(
                    String.format("No product found with id: %d", id), 1);
        }
    }

    @Transactional
    public void bulkUpdateStock(List<Product> products) {
        logger.debug("Performing bulk stock update for {} products", products.size());

        jdbcTemplate.batchUpdate(
                "UPDATE products SET stock_quantity = ?, updated_at = ? WHERE id = ? AND is_active = true",
                products,
                products.size(),
                (PreparedStatement ps, Product product) -> {
                    ps.setInt(1, product.getStockQuantity());
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setLong(3, product.getId());
                }
        );
    }
}