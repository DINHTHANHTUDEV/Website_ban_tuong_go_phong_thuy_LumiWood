package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.ProductUpdateDTO;
import com.example.websitebantuonggolumiwood.entity.Category;
import com.example.websitebantuonggolumiwood.entity.Product;
import com.example.websitebantuonggolumiwood.repository.CategoriesRepositories;
import com.example.websitebantuonggolumiwood.repository.ProductRepository;
import com.example.websitebantuonggolumiwood.service.CategoriesService;
import com.example.websitebantuonggolumiwood.service.ProductsService;
import com.example.websitebantuonggolumiwood.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.Normalizer;

@RestController
@RequestMapping("/api/products/admin")
@CrossOrigin("http://localhost:5173")
public class AdminProductsController {
    private final ProductsService serviceProduct;
    private final ProductRepository productRepository;
    private final ProductsService productsService;
    private final CategoriesRepositories categoriesRepositories;
    private final CategoriesService categoriesService;

    public AdminProductsController(ProductsService serviceProduct, ProductRepository productRepository, ProductsService productsService, CategoriesRepositories categoriesRepositories, CategoriesService categoriesService) {
        this.serviceProduct = serviceProduct;
        this.productRepository = productRepository;
        this.productsService = productsService;
        this.categoriesRepositories = categoriesRepositories;
        this.categoriesService = categoriesService;
    }


    @GetMapping("")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Specification<Product> spec = ProductSpecification.filter(keyword, categoryId, isActive);
        Page<Product> products = productRepository.findAll(spec, pageable);

        return ResponseEntity.ok(products); // Trả thẳng entity
    }
    // Auto create slug product
    private String generateSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return withoutDiacritics.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // bỏ ký tự đặc biệt
                .replaceAll("\\s+", "-")        // thay khoảng trắng bằng dấu -
                .replaceAll("-{2,}", "-")       // tránh dấu -- liên tiếp
                .replaceAll("^-|-$", "");       // bỏ dấu - đầu/cuối
    }
    // add product new
    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody ProductUpdateDTO productDTO) {
        Product product = new Product();

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(BigDecimal.valueOf(productDTO.getPrice()));
        product.setStock(productDTO.getStock());
        product.setImage_url(productDTO.getImageUrl());
        product.setDimensions(productDTO.getDimensions());
        product.setMaterial(productDTO.getMaterial());
        product.setIsActive(productDTO.getIsActive());

        // Xử lý category: lấy entity category từ categoryId
        if (productDTO.getCategoryId() != null) {
            Category category = categoriesService.findById(productDTO.getCategoryId());
            product.setCategory(category);
        }

        // Sinh slug nếu cần
        if (product.getName() != null && (product.getSlug() == null || product.getSlug().isBlank())) {
            String slug = generateSlug(product.getName());
            product.setSlug(slug);
        }

        return serviceProduct.save(product);
    }
    @GetMapping("getProductById/{id}")
    public Product getProductBySlug(@PathVariable Integer id) {
        return serviceProduct.getProductById(id);
    }


    @PutMapping("UpdateProducts/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody ProductUpdateDTO dto) {
        Product product = productsService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoriesRepositories.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(BigDecimal.valueOf(dto.getPrice()));
        product.setStock(dto.getStock());
        product.setImage_url(dto.getImageUrl());
        product.setDimensions(dto.getDimensions());
        product.setMaterial(dto.getMaterial());
        product.setCategory(category);
        product.setIsActive(dto.getIsActive());

        if (product.getName() != null && (product.getSlug() == null || product.getSlug().isBlank())) {
            String slug = generateSlug(product.getName());
            product.setSlug(slug);
        }

        productsService.save(product);

        return ResponseEntity.ok(product);  // Trả về sản phẩm mới cập nhật cho dễ debug
    }
}
