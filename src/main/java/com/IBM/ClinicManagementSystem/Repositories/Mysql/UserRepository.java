package com.IBM.ClinicManagementSystem.Repositories.Mysql;

import com.IBM.ClinicManagementSystem.Models.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Page<User> findByNameContainingIgnoreCase(String name,Pageable pageable);
    Page<User> findByGender(User.Gender gender, Pageable pageable);
    Page<User> findByStatus(User.Status status, Pageable pageable);
    Page<User> findByAddressContainingIgnoreCase(String address, Pageable pageable);
    Page<User> findAll(Specification<User> userSpecification,Pageable pageable);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);
}
