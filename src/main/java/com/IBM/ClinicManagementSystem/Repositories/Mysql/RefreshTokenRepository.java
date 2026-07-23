package com.IBM.ClinicManagementSystem.Repositories.Mysql;

import com.IBM.ClinicManagementSystem.Models.Entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByRefreshToken(String token);

    List<RefreshToken> findAllByUser_Id(Long userId);

    void deleteByRefreshToken(String token);

    void deleteAllByUser_Id(Long userId);

    boolean existsByRefreshToken(String token);
}
