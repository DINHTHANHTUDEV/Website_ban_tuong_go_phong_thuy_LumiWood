package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserManagementRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    // JpaSpecificationExecutor giúp build truy vấn động, filter theo tier, status, search
}
