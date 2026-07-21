package com.IBM.ClinicManagementSystem.Services.Security;

import com.IBM.ClinicManagementSystem.DTOs.Mail.MailDTO;
import com.IBM.ClinicManagementSystem.DTOs.Security.*;
import com.IBM.ClinicManagementSystem.Mappers.Mail.MailMapper;
import com.IBM.ClinicManagementSystem.Models.Entities.RefreshToken;
import com.IBM.ClinicManagementSystem.Models.Entities.User;
import com.IBM.ClinicManagementSystem.Repositories.Mysql.RefreshTokenRepository;
import com.IBM.ClinicManagementSystem.Services.Mail.MailService;
import com.IBM.ClinicManagementSystem.Services.Site.UserService;
import com.IBM.ClinicManagementSystem.Utils.Helper.Helper;
import com.IBM.ClinicManagementSystem.Utils.JWT.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final MailService mailService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional

    public String register(RegisterDTO request){
        if(userService.userExistsByEmail(request.getEmail())){
            throw new IllegalArgumentException("This Email Already Exists !");
        }
        if(userService.userExistsByPhone(request.getPhone())){
            throw new IllegalArgumentException("This Phone Number Already Exists !");
        }
        try{
            String verificationCode = Helper.generateCode();
            User user = User.builder()
                    .name(request.getFirstName()+" "+request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .birthdate(request.getBirthdate())
                    .gender(request.getGender())
                    .role(request.getRole())
                    .verificationCode(verificationCode)
                    .build();
            userService.saveUser(user);
            mailService.sendCodeToViaEmail(MailMapper.toDTO(user.getEmail(),verificationCode));
            return "Please Check Your Email to Get Verification Code !!";
        }catch (Exception e){
            e.printStackTrace();
            log.error("Error occurred while registering user", e);
            throw new RuntimeException("An unexpected error occurred. Please try again later.");
        }
    }
    public AccessTokenDTO login(LoginDTO request, HttpServletResponse response){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );
        var user = userService.getUserEntityByEmail(request.getEmail());
        if(!user.getStatus().equals(User.Status.ACTIVE)){
            throw new IllegalArgumentException("Your account is currently " + user.getStatus().name().toLowerCase() + ". Please verify or contact support.");
        }
        if(refreshTokenRepository.findByUser_Id(user.getId()).isPresent()){
            throw new IllegalArgumentException("This Account Already Login If You Want New Token You Could Use refresh-token end point !");
        }
        String token = jwtUtil.generateToken(user.getId(),user.getEmail(),user.getRole().name(),user.getStatus().name());
        generateRefreshTokenCookies(jwtUtil.generateRefreshToken(user.getId()),response);
        return new AccessTokenDTO(token);
    }
    @Transactional
    public String verifyAccount(MailDTO dto){
        var user = userService.getUserEntityByEmail(dto.getEmail());
        if(user.getEnabled() || user.getVerificationCode() == null){
            throw new IllegalArgumentException("This account already verified !");
        }
        if(!user.getVerificationCode().equals(dto.getVerificationCode())){
            throw new IllegalArgumentException("Verification Code Didn't Match !!");
        }
        user.setVerificationCode(null);
        userService.changeUserStatus(user.getId(), User.Status.ACTIVE);
        return "Your Account Has Been Verified Successfully !";
    }

    public String resendVerificationCode(ResetVerificationCodeDTO request){
        var user = userService.getUserEntityByEmail(request.getEmail());
        if(user.getVerificationCode() == null || user.getEnabled()){
            throw new IllegalArgumentException("This account is already verified !");
        }
        try{
            String verificationCode = Helper.generateCode();
            user.setVerificationCode(verificationCode);
            userService.saveUser(user);
            mailService.sendCodeToViaEmail(MailMapper.toDTO(user.getEmail(),verificationCode));
            return "Please Check Your Email to Get Verification Code !!";
        }catch (Exception e){
            throw new RuntimeException("Something Wrong happened please try again ",e.getCause());
        }
    }
    @Transactional
    public String resetPassword(ResetPasswordDTO request){
        var user = userService.getUserEntityByEmail(request.getEmail());

        if(!user.getStatus().equals(User.Status.ACTIVE)){
            throw new IllegalArgumentException("You Couldn't Change Your Current Password Because Your Account Is "+user.getStatus().name());
        }
        if(!request.getPhone().equals(user.getPhone())){
            throw new IllegalArgumentException("Invalid Phone Number !");
        }
        if(!request.getNewPassword().equals(request.getConfirmedNewPassword())){
            throw new IllegalArgumentException("Passwords Don't Match !");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.saveUser(user);
        return "Password Changed Successfully !";
    }
    public String logout(HttpServletRequest request, HttpServletResponse response){
        var refreshToken = extractRefreshTokenFromCookies(request);
        if(refreshToken != null){
            var token = jwtUtil.findRefreshToken(refreshToken);
            if(token != null){
                jwtUtil.deleteRefreshTokenByUserId(token.getUser().getId());
            }
        }
        deleteRefreshTokenCookies(response);
        return "Logged out successfully !";
    }

    public void generateRefreshTokenCookies(RefreshToken refreshToken, HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken",refreshToken.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // if not working it because https issues so make it false
        cookie.setPath("/");
        long maxAgeSeconds = refreshToken.getExpirationDate().getEpochSecond() - Instant.now().getEpochSecond();
        cookie.setMaxAge((int)maxAgeSeconds);
        response.addCookie(cookie);
    }
    public void deleteRefreshTokenCookies(HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken",null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // if not working it because https issues so make it false
        cookie.setPath("/api/v1/auth/refresh-token");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    public String extractRefreshTokenFromCookies(HttpServletRequest request){
        String refreshToken = null;
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if("refreshToken".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                }
            }
        }
        if(refreshToken == null){
            throw new IllegalArgumentException("Refresh Token Not Found !");
        }
        return refreshToken;
    }
    public AccessTokenDTO refreshAccessToken(HttpServletRequest request){
        var refreshToken = extractRefreshTokenFromCookies(request);
        RefreshToken token = jwtUtil.findRefreshToken(refreshToken);
        jwtUtil.verifyExpiration(token);
        var user = token.getUser();
        var newAccessToken = jwtUtil.generateToken(user.getId(),user.getEmail(),user.getRole().name(),user.getStatus().name());
        return new AccessTokenDTO(newAccessToken);
    }
}
