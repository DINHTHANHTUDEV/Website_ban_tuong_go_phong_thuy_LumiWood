package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductsRepositories extends JpaRepository<ProductsEntity, Integer>, JpaSpecificationExecutor<ProductsEntity> {
    Optional<ProductsEntity> findBySlug(String slug);

    @Query("SELECT DISTINCT p.material FROM ProductsEntity p WHERE p.material IS NOT NULL")
    List<String> findDistinctMaterials();
    // lấy product = slug categories
    @Query("SELECT p FROM ProductsEntity p WHERE p.category.slug = :slug")
    List<ProductsEntity> findByCategorySlug(@Param("slug") String slug);
}
