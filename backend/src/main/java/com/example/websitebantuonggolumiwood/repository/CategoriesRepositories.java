package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepositories extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
}
