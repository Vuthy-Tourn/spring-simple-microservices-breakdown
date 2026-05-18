package dev.sample.productservice.repository;

import dev.sample.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by category
    List<Product> findByCategory(String category);

    // Find by name containing (case-insensitive search)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products within a price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find in-stock products only
    List<Product> findByStockGreaterThan(int stock);

    // Custom JPQL query — find by category and in stock
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.stock > 0")
    List<Product> findAvailableByCategory(@Param("category") String category);

    // Check if name already exists (for duplicate prevention)
    boolean existsByNameIgnoreCase(String name);
}
