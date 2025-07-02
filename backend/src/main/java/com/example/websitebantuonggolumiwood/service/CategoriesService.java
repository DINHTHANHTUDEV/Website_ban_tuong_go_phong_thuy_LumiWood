package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.Category;
import com.example.websitebantuonggolumiwood.repository.CategoriesRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {
    private final CategoriesRepositories repositoriesCategories;

    public CategoriesService(CategoriesRepositories repositoriesCategories) {
        this.repositoriesCategories = repositoriesCategories;
    }

    public List<Category> getAllCategories() {
        return repositoriesCategories.findAll();
    }

    public Category getCategoryBySlug(String slug) {
        return repositoriesCategories.findBySlug(slug).get();
    }

    public Category findById(Long categoryId) {
        return repositoriesCategories.findById(categoryId).get();
    }
}
