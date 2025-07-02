package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.entity.Product;
import com.example.websitebantuonggolumiwood.repository.CategoriesRepositories;
import com.example.websitebantuonggolumiwood.repository.ProductRepository;
import com.example.websitebantuonggolumiwood.service.CategoriesService;
import com.example.websitebantuonggolumiwood.service.ProductsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/public")
@CrossOrigin("http://localhost:5173")

public class ProductsController {
    private final ProductsService serviceProduct;
    private final ProductRepository productRepository;
    private final ProductsService productsService;
    private final CategoriesRepositories categoriesRepositories;
    private final CategoriesService categoriesService;

    public ProductsController(ProductsService serviceProduct, ProductRepository productRepository, ProductsService productsService, CategoriesRepositories categoriesRepositories, CategoriesService categoriesService) {
        this.serviceProduct = serviceProduct;
        this.productRepository = productRepository;
        this.productsService = productsService;
        this.categoriesRepositories = categoriesRepositories;
        this.categoriesService = categoriesService;
    }

//phân trang, hien thi san pham
    @GetMapping
    public ResponseEntity<Page<Product>> filterProducts(
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String materials,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sizeCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<Product> result = productsService.filterProducts(
                categories, minPrice, maxPrice, materials,
                page, size, sortBy, sortDir, keyword, sizeCategory
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/slug/{slug}")
    public Product getProductsBySlug(@PathVariable String slug) {
        return serviceProduct.getProductsBySlug(slug);
    }
    // get all chất liệu
    @GetMapping("/materials")
    public List<String> getProductsByMaterials() {
        return productRepository.findDistinctMaterials();
    }
    @GetMapping("/category/{slug}")
    public List<Product> getProductsByCategory(@PathVariable String slug) {
        return serviceProduct.getProductsByCategorySlug(slug);
    }








}
