package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressesRepository extends JpaRepository<Addresses, Long> {

    // Tùy chọn: lấy tất cả địa chỉ của user theo userId, tiện cho service gọi
    List<Addresses> findByUserUserId(Long userId);
}
