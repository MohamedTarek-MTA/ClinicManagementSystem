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
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

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
                    .verificationCodeExpirationTime(LocalDateTime.now().plusMinutes(15))
                    .build();
            userService.saveUser(user);
            mailService.sendCodeToViaEmail(MailMapper.toDTO(user.getEmail(),verificationCode));
            return "Please Check Your Email to Get Verification Code, It's Valid For Just 15 Minutes";
        }catch (Exception e){
            e.printStackTrace();
            log.error("Error occurred while registering user", e);
            throw new RuntimeException("An unexpected error occurred. Please try again later.");
        }
    }
    @Transactional
    public AccessTokenDTO login(LoginDTO request,
                                HttpServletRequest httpRequest,
                                HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userService.getUserEntityByEmail(request.getEmail());

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new IllegalArgumentException(
                    "Your account is currently "
                            + user.getStatus().name().toLowerCase()
                            + ". Please verify or contact support."
            );
        }

        String accessToken = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );

        RefreshToken refreshToken = jwtUtil.generateRefreshToken(
                user.getId(),
                Helper.getClientIp(httpRequest),
                Helper.getDeviceName(httpRequest),
                httpRequest.getHeader("User-Agent")
        );

        generateRefreshTokenCookie(refreshToken, response);

        return new AccessTokenDTO(accessToken);
    }    @Transactional
    public String verifyAccount(MailDTO dto){
        var user = userService.getUserEntityByEmail(dto.getEmail());
        if(user.getEnabled() || user.getVerificationCode() == null){
            throw new IllegalArgumentException("This account already verified !");
        }
        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpirationTime())) {
            throw new IllegalArgumentException("Verification code has expired.");
        }
        if(!user.getVerificationCode().equals(dto.getVerificationCode())){
            throw new IllegalArgumentException("Verification Code Didn't Match !!");
        }
        user.setVerificationCode(null);
        user.setVerificationCodeExpirationTime(null);
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
            user.setVerificationCodeExpirationTime(LocalDateTime.now().plusMinutes(15));
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
    @Transactional
    public String logout(Long userId,
            HttpServletRequest request,
            HttpServletResponse response
    ){

        String refreshToken = extractRefreshTokenFromCookies(request);

        if (refreshToken != null) {

            RefreshToken token = jwtUtil.findRefreshToken(refreshToken);

            if (!token.getUser().getId().equals(userId)) {
                throw new AccessDeniedException("Invalid refresh token.");
            }

            refreshTokenRepository.delete(token);
        }

        deleteRefreshTokenCookie(response);

        return "Logged out successfully.";
    }
    @Transactional
    public void generateRefreshTokenCookie(RefreshToken refreshToken,
                                           HttpServletResponse response) {

        long maxAgeSeconds = refreshToken.getExpiryDate().getEpochSecond()
                - Instant.now().getEpochSecond();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    public void deleteRefreshTokenCookie(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    private String extractRefreshTokenFromCookies(
            HttpServletRequest request
    ){

        if(request.getCookies()==null)
            return null;

        for(Cookie cookie : request.getCookies()){

            if("refreshToken".equals(cookie.getName())){

                return cookie.getValue();
            }

        }

        throw new IllegalArgumentException("Refresh token missing.");
    }
    @Transactional
    public AccessTokenDTO refreshAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshTokenString =
                extractRefreshTokenFromCookies(request);

        RefreshToken refreshToken =
                jwtUtil.findRefreshToken(refreshTokenString);

        jwtUtil.verifyExpiration(refreshToken);

        if (refreshToken.getRevoked()) {
            throw new IllegalArgumentException("Refresh token revoked.");
        }

        User user = refreshToken.getUser();

        refreshTokenRepository.delete(refreshToken);

        RefreshToken newRefresh =
                jwtUtil.generateRefreshToken(
                        user.getId(),
                        request.getRemoteAddr(),
                        refreshToken.getDeviceName(),
                        request.getHeader("User-Agent")
                );

        generateRefreshTokenCookie(newRefresh,response);

        String accessToken =
                jwtUtil.generateToken(
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getStatus().name()
                );

        return new AccessTokenDTO(accessToken);
    }
    @Transactional
    public String logoutAll(Long userId,
                            HttpServletRequest request,
                            HttpServletResponse response) {

        String refreshToken = extractRefreshTokenFromCookies(request);

        RefreshToken token = jwtUtil.findRefreshToken(refreshToken);

        if (!token.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Invalid refresh token.");
        }

        refreshTokenRepository.deleteAllByUser_Id(userId);

        deleteRefreshTokenCookie(response);

        return "Logged out successfully from all devices.";
    }
}
