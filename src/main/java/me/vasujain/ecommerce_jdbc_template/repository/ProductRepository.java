package me.vasujain.ecommerce_jdbc_template.repository;

import me.vasujain.ecommerce_jdbc_template.mapper.ProductRowMapper;
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
            INSERT INTO products (name, description, price, stock_quantity)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_PRODUCT = """
            UPDATE products
            SET name = ?, description = ?, price = ?, stock_quantity = ?
            WHERE id = ?
            """;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRowMapper = new ProductRowMapper();
    }

    public List<Product> findAll() {
        logger.debug("Fetching all products");
        return jdbcTemplate.query(SELECT_ALL, productRowMapper);
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
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_PRODUCT,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQuantity());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            product.setId(key.longValue());
        }
        return product;
    }

    @Transactional
    public Product update(Product product) {
        logger.debug("Updating product: {}", product);
        int updatedRows = jdbcTemplate.update(
                UPDATE_PRODUCT,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
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
        logger.debug("Deleting product with id: {}", id);
        int updatedRows = jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException(
                    String.format("No product found with id: %d", id), 1);
        }
    }
}
