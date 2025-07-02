package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);

    @Query("SELECT DISTINCT p.material FROM Product p WHERE p.material IS NOT NULL")
    List<String> findDistinctMaterials();
    // lấy product = slug categories
    @Query("SELECT p FROM Product p WHERE p.category.slug = :slug")
    List<Product> findByCategorySlug(@Param("slug") String slug);

    List<Product> findAllById(Iterable<Integer> ids); // Tìm danh sách sản phẩm theo ID
}
