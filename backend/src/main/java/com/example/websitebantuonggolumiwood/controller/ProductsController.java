package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.ProductUpdateDTO;
import com.example.websitebantuonggolumiwood.entity.CategoriesEntity;
import com.example.websitebantuonggolumiwood.entity.ProductsEntity;
import com.example.websitebantuonggolumiwood.repository.CategoriesRepositories;
import com.example.websitebantuonggolumiwood.repository.ProductsRepositories;
import com.example.websitebantuonggolumiwood.service.CategoriesService;
import com.example.websitebantuonggolumiwood.service.ProductsService;
import com.example.websitebantuonggolumiwood.specification.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/public")
@CrossOrigin("http://localhost:5173")

public class ProductsController {
    private final ProductsService serviceProduct;
    private final ProductsRepositories productsRepositories;
    private final ProductsService productsService;
    private final CategoriesRepositories categoriesRepositories;
    private final CategoriesService categoriesService;

    public ProductsController(ProductsService serviceProduct, ProductsRepositories productsRepositories, ProductsService productsService, CategoriesRepositories categoriesRepositories, CategoriesService categoriesService) {
        this.serviceProduct = serviceProduct;
        this.productsRepositories = productsRepositories;
        this.productsService = productsService;
        this.categoriesRepositories = categoriesRepositories;
        this.categoriesService = categoriesService;
    }


    @GetMapping
    public ResponseEntity<Page<ProductsEntity>> filterProducts(
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
        Page<ProductsEntity> result = productsService.filterProducts(
                categories, minPrice, maxPrice, materials,
                page, size, sortBy, sortDir, keyword, sizeCategory
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/slug/{slug}")
    public ProductsEntity getProductsBySlug(@PathVariable String slug) {
        return serviceProduct.getProductsBySlug(slug);
    }
    // get all chất liệu
    @GetMapping("/materials")
    public List<String> getProductsByMaterials() {
        return productsRepositories.findDistinctMaterials();
    }
    @GetMapping("/category/{slug}")
    public List<ProductsEntity> getProductsByCategory(@PathVariable String slug) {
        return serviceProduct.getProductsByCategorySlug(slug);
    }








}
