package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.ProductsEntity;
import com.example.websitebantuonggolumiwood.repository.ProductsRepositories;
import com.example.websitebantuonggolumiwood.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {
    private final ProductsRepositories repositoriesProduct;
    private final ProductsRepositories productsRepositories;

    public ProductsService(ProductsRepositories repositoriesProduct, ProductsRepositories productsRepositories) {
        this.repositoriesProduct = repositoriesProduct;
        this.productsRepositories = productsRepositories;
    }



    public ProductsEntity getProductsBySlug(String slug) {
        return repositoriesProduct.findBySlug(slug).get();
    }

    //        public Page<ProductsEntity> filterProducts(List<Integer> categories, Double minPrice, Double maxPrice, String materials,
//                                                   int page, int sizePerPage, String sortBy, String sortDir, String keyword, String sizeCategory) {
//
//            // Validate sort field
//            List<String> allowedSortFields = List.of("price", "name", "createdAt");
//            if (!allowedSortFields.contains(sortBy)) {
//                sortBy = "price"; // default field
//            }
//
//            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//            Pageable pageable = PageRequest.of(page, sizePerPage, Sort.by(direction, sortBy));
//
//            Specification<ProductsEntity> spec = ProductSpecification.filterBy(categories, minPrice, maxPrice, materials, keyword, sizeCategory);
//            return productsRepositories.findAll(spec, pageable);
//
//        }
    public Page<ProductsEntity> filterProducts(List<Integer> categories, Double minPrice, Double maxPrice, String materials,
                                               int page, int sizePerPage, String sortBy, String sortDir,
                                               String keyword, String sizeCategory) {

        // Validate sort field
        List<String> allowedSortFields = List.of("price", "name", "createdAt");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "price";
        }

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // Lấy tất cả dữ liệu theo filter (không phân trang)
        Specification<ProductsEntity> spec = ProductSpecification.filterBy(categories, minPrice, maxPrice, materials, keyword, null);
        List<ProductsEntity> allProducts = productsRepositories.findAll(spec, sort);

        // Lọc theo sizeCategory nếu cần
        if (sizeCategory != null && !sizeCategory.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(product -> {
                        Double volume = ProductSpecification.parseVolume(product.getDimensions());
                        if (volume == null) return false;

                        switch (sizeCategory.toLowerCase()) {
                            case "small": return volume < 1000;
                            case "medium": return volume >= 1000 && volume < 10000;
                            case "large": return volume >= 10000;
                            default: return true;
                        }
                    }).toList();
        }

        // Phân trang thủ công
        int total = allProducts.size();
        int start = page * sizePerPage;
        int end = Math.min(start + sizePerPage, total);

        List<ProductsEntity> content;
        if (start <= end) {
            content = allProducts.subList(start, end);
        } else {
            content = List.of(); // trang vượt quá, trả list rỗng
        }

        Pageable pageable = PageRequest.of(page, sizePerPage, sort);
        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }




    public List<ProductsEntity> getProductsByCategorySlug(String slug) {
        return repositoriesProduct.findByCategorySlug(slug);
    }

    public ProductsEntity save(ProductsEntity product) {
        return repositoriesProduct.save(product);
    }

    public ProductsEntity getProductById(Integer id) {
        return repositoriesProduct.findById(id).get();
    }
    public Optional<ProductsEntity> findById(int id) {
        return productsRepositories.findById(id);
    }
}

