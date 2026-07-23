package com.IBM.ClinicManagementSystem.Utils.JWT;

import com.IBM.ClinicManagementSystem.Configurations.JwtConfig;
import com.IBM.ClinicManagementSystem.Models.Entities.RefreshToken;
import com.IBM.ClinicManagementSystem.Models.Entities.User;
import com.IBM.ClinicManagementSystem.Repositories.Mysql.RefreshTokenRepository;
import com.IBM.ClinicManagementSystem.Services.Site.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtConfig jwtConfig;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    public String generateToken(Long id,String email,String role,String status){
        long expirationTime = jwtConfig.getExpiration();
        try{
            String token = Jwts.builder()
                    .setSubject(email)
                    .claim("id",id)
                    .claim("role",role)
                    .claim("status",status)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis()+expirationTime))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
            return token;
        }
        catch (Exception e){
            return null;
        }

    }
    private Key getSigningKey(){
        byte[] keyBytes = Base64.getDecoder().decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean validateToken(String token, UserDetails userDetails){
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public RefreshToken generateRefreshToken(
            Long userId,
            String ip,
            String device,
            String userAgent
    ) {

        User user = userService.getUserEntityById(userId);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expiryDate(
                        Instant.now().plusMillis(jwtConfig.getRefreshExpiration())
                )
                .deviceName(device)
                .ipAddress(ip)
                .userAgent(userAgent)
                .build();

        return refreshTokenRepository.save(token);
    }
    public RefreshToken findRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->
                new IllegalArgumentException("Refresh Token Not Found !"));
    }
    public void verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token was expired. Please make a new sign in request !");
        }
    }

}
