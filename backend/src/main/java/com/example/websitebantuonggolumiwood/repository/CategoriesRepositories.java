package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.CategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepositories extends JpaRepository<CategoriesEntity, Long> {
    Optional<CategoriesEntity> findBySlug(String slug);
}
