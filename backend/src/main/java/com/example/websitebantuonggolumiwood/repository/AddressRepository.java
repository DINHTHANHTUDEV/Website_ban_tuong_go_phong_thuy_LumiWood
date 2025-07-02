package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Address;
import com.example.websitebantuonggolumiwood.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUser_UserId(Long userId);  // Sửa từ findByUserUserId thành findByUser_UserId

    Optional<Address> findByUserAndIsDefaultShippingTrue(User user);

//    List<Address> findByUserId(Integer userId); // Tìm danh sách địa chỉ theo userId
}
