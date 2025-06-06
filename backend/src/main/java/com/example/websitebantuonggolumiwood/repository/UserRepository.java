package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo username trả về Optional<User>
    Optional<User> findByUsername(String username);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

//    boolean existsByEmail(String email);
}
