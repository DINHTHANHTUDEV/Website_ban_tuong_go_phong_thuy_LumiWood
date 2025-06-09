package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.ShippingMethodDTO;
import com.example.websitebantuonggolumiwood.entity.ShippingMethod;
import com.example.websitebantuonggolumiwood.repository.ShippingMethodAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/shipping-methods")
//@CrossOrigin(origins = "http://localhost:5174")
public class ShippingMethodController {

    @Autowired
    private ShippingMethodAdminRepository shippingMethodAdminRepository;

    @GetMapping
    public Page<ShippingMethodDTO> getAll(Pageable pageable) {
        Page<ShippingMethod> page = shippingMethodAdminRepository.findAll(pageable);
        return page.map(method -> new ShippingMethodDTO(
                method.getId(),
                method.getName(),
                method.getDescription(),
                method.getBaseCost(),
                method.getEstimatedDaysMin(),
                method.getEstimatedDaysMax(),
                method.getIsActive()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingMethodDTO> getById(@PathVariable Integer id) {
        return shippingMethodAdminRepository.findById(id)
                .map(method -> {
                    ShippingMethodDTO response = new ShippingMethodDTO(
                            method.getId(),
                            method.getName(),
                            method.getDescription(),
                            method.getBaseCost(),
                            method.getEstimatedDaysMin(),
                            method.getEstimatedDaysMax(),
                            method.getIsActive()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createShippingMethod(@RequestBody ShippingMethod newMethod) {
        ShippingMethod saved = shippingMethodAdminRepository.save(newMethod);
        return ResponseEntity.ok(Map.of("message", "Thêm mới thành công", "id", saved.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShippingMethod(@PathVariable Integer id, @RequestBody ShippingMethod updatedMethod) {
        return shippingMethodAdminRepository.findById(id)
                .map(method -> {
                    method.setName(updatedMethod.getName());
                    method.setDescription(updatedMethod.getDescription());
                    method.setBaseCost(updatedMethod.getBaseCost());
                    method.setEstimatedDaysMin(updatedMethod.getEstimatedDaysMin());
                    method.setEstimatedDaysMax(updatedMethod.getEstimatedDaysMax());
                    method.setIsActive(updatedMethod.getIsActive());
                    shippingMethodAdminRepository.save(method);
                    return ResponseEntity.ok(Map.of("message", "Cập nhật thành công"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShippingMethod(@PathVariable Integer id) {
        return shippingMethodAdminRepository.findById(id)
                .map(method -> {
                    shippingMethodAdminRepository.delete(method);
                    return ResponseEntity.ok(Map.of("message", "Xóa thành công"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
