package com.example.websitebantuonggolumiwood.controller;


import com.example.websitebantuonggolumiwood.entity.Category;
import com.example.websitebantuonggolumiwood.service.CategoriesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/categories")
@CrossOrigin("http://localhost:5173")
public class CategoriesController {
    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }
    @GetMapping
    public  List<Category>getCategories() {
        return categoriesService.getAllCategories();

    }
    @GetMapping("/slug/{slug}")
    public Category getCategoryBySlug(@PathVariable String slug) {
        return categoriesService.getCategoryBySlug(slug);
    }


}
