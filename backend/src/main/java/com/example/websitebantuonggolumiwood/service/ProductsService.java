package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.Product;
import com.example.websitebantuonggolumiwood.repository.ProductRepository;
import com.example.websitebantuonggolumiwood.specification.ProductSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {
    private final ProductRepository repositoriesProduct;
    private final ProductRepository productRepository;

    public ProductsService(ProductRepository repositoriesProduct, ProductRepository productRepository) {
        this.repositoriesProduct = repositoriesProduct;
        this.productRepository = productRepository;
    }



    public Product getProductsBySlug(String slug) {
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
    public Page<Product> filterProducts(List<Integer> categories, Double minPrice, Double maxPrice, String materials,
                                        int page, int sizePerPage, String sortBy, String sortDir,
                                        String keyword, String sizeCategory) {

        // Validate sort field
        List<String> allowedSortFields = List.of("price", "name", "createdAt");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "price";
        }

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // Lấy toàn bộ sản phẩm (chưa lọc keyword)
        Specification<Product> spec = ProductSpecification.filterBy(categories, minPrice, maxPrice, materials);
        List<Product> allProducts = productRepository.findAll(spec, sort);

        // Lọc keyword không dấu
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordUnaccented = ProductSpecification.removeVietnameseAccents(keyword.toLowerCase());

            allProducts = allProducts.stream()
                    .filter(p -> {
                        String name = ProductSpecification.removeVietnameseAccents(
                                p.getName() != null ? p.getName().toLowerCase() : "");
                        String desc = ProductSpecification.removeVietnameseAccents(
                                p.getDescription() != null ? p.getDescription().toLowerCase() : "");
                        return name.contains(keywordUnaccented) || desc.contains(keywordUnaccented);
                    })
                    .toList();
        }

        // Lọc theo sizeCategory nếu có
        if (sizeCategory != null && !sizeCategory.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(product -> {
                        Double volume = ProductSpecification.parseVolume(product.getDimensions());
                        if (volume == null) return false;

                        return switch (sizeCategory.toLowerCase()) {
                            case "small" -> volume < 1000;
                            case "medium" -> volume >= 1000 && volume < 10000;
                            case "large" -> volume >= 10000;
                            default -> true;
                        };
                    }).toList();
        }

        // Phân trang thủ công
        int total = allProducts.size();
        int start = page * sizePerPage;
        int end = Math.min(start + sizePerPage, total);
        List<Product> content = (start <= end) ? allProducts.subList(start, end) : List.of();

        Pageable pageable = PageRequest.of(page, sizePerPage, sort);
        return new PageImpl<>(content, pageable, total);
    }







    public List<Product> getProductsByCategorySlug(String slug) {
        return repositoriesProduct.findByCategorySlug(slug);
    }

    public Product save(Product product) {
        return repositoriesProduct.save(product);
    }

    public Product getProductById(Integer id) {
        return repositoriesProduct.findById(id).get();
    }
    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }


}

