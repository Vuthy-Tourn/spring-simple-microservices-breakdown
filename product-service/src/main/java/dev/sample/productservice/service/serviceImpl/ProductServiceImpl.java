package dev.sample.productservice.serviceImpl;

import dev.sample.productservice.dto.ProductRequest;
import dev.sample.productservice.dto.ProductResponse;
import dev.sample.productservice.exception.DuplicateResourceException;
import dev.sample.productservice.exception.ResourceNotFoundException;
import dev.sample.productservice.model.Product;
import dev.sample.productservice.repository.ProductRepository;
import dev.sample.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with name: {}", request.getName());

        if (productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Product with name '" + request.getName() + "' already exists");
        }

        Product product = mapToEntity(request);
        Product saved = productRepository.save(product);

        log.info("Product created successfully with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = findOrThrow(id);
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        log.info("Fetching products with price between {} and {}", min, max);
        return productRepository.findByPriceBetween(min, max)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getInStockProducts() {
        log.info("Fetching in-stock products");
        return productRepository.findByStockGreaterThan(0)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product existing = findOrThrow(id);

        // Check duplicate name only if name actually changed
        if (!existing.getName().equalsIgnoreCase(request.getName())
                && productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Product with name '" + request.getName() + "' already exists");
        }

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategory(request.getCategory());

        Product updated = productRepository.save(existing);
        log.info("Product updated successfully with id: {}", updated.getId());
        return mapToResponse(updated);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = findOrThrow(id);
        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    private Product mapToEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
