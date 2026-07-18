package com.IBM.ClinicManagementSystem.Repositories.Mysql;

import com.IBM.ClinicManagementSystem.Models.Entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUser_Id(Long userId);
}
