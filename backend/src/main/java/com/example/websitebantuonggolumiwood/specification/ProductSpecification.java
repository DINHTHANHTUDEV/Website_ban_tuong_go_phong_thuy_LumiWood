package com.example.websitebantuonggolumiwood.specification;

import com.example.websitebantuonggolumiwood.entity.ProductsEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<ProductsEntity> filterBy(
            List<Integer> categories,
            Double minPrice,
            Double maxPrice,
            String material
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isActive"), true));

            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (material != null && !material.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("material"), material));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static String removeVietnameseAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }


    public static Double parseVolume(String dimensions) {
        if (dimensions == null || dimensions.isEmpty()) return null;

        try {
            String[] parts = dimensions.toLowerCase().split("x");
            if (parts.length != 3) return null;

            double length = Double.parseDouble(parts[0].trim());
            double width = Double.parseDouble(parts[1].trim());
            double height = Double.parseDouble(parts[2].trim());

            return length * width * height;
        } catch (Exception e) {
            return null;
        }
    }
    // lọc product admin
    public static Specification<ProductsEntity> filter(String keyword, Integer categoryId, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), kw);
                Predicate slugPredicate = cb.like(cb.lower(root.get("slug")), kw);
                Predicate descPredicate = cb.like(cb.lower(root.get("description")), kw);
                predicates.add(cb.or(namePredicate, slugPredicate, descPredicate));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

