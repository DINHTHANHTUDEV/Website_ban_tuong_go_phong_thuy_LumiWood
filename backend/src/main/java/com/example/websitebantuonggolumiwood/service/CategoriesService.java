package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.CategoriesEntity;
import com.example.websitebantuonggolumiwood.repository.CategoriesRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {
    private final CategoriesRepositories repositoriesCategories;

    public CategoriesService(CategoriesRepositories repositoriesCategories) {
        this.repositoriesCategories = repositoriesCategories;
    }

    public List<CategoriesEntity> getAllCategories() {
        return repositoriesCategories.findAll();
    }

    public CategoriesEntity getCategoryBySlug(String slug) {
        return repositoriesCategories.findBySlug(slug).get();
    }

    public CategoriesEntity findById(Long categoryId) {
        return repositoriesCategories.findById(categoryId).get();
    }
}
