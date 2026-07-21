package com.IBM.ClinicManagementSystem.Controllers.Rest.Auth;

import com.IBM.ClinicManagementSystem.DTOs.Mail.MailDTO;
import com.IBM.ClinicManagementSystem.DTOs.Security.LoginDTO;
import com.IBM.ClinicManagementSystem.DTOs.Security.RegisterDTO;
import com.IBM.ClinicManagementSystem.DTOs.Security.ResetPasswordDTO;
import com.IBM.ClinicManagementSystem.DTOs.Security.ResetVerificationCodeDTO;
import com.IBM.ClinicManagementSystem.RateLimiter.RateLimit;
import com.IBM.ClinicManagementSystem.Services.Security.AuthService;
import com.IBM.ClinicManagementSystem.Utils.Helper.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    @RateLimit(maxRequests = 5, timeWindowMs = 30000)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO request){
        return ResponseEntity.ok(ApiResponse.success(authService.register(request)));
    }
    @PostMapping("/verify-account")
    @RateLimit(maxRequests = 1, timeWindowMs = 1500)
    public ResponseEntity<?> verifyAccount(@Valid @RequestBody MailDTO dto){
        return ResponseEntity.ok(ApiResponse.success(authService.verifyAccount(dto)));
    }
    @PostMapping("/login")
    @RateLimit(maxRequests = 5, timeWindowMs = 30000)
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request, HttpServletResponse response){
        return ResponseEntity.ok(ApiResponse.success(authService.login(request,response)));
    }
    @PostMapping("/logout")
    @RateLimit(maxRequests = 5, timeWindowMs = 60000)
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok(ApiResponse.success(authService.logout(request,response)));
    }
    @PostMapping("/refresh-token")
    @RateLimit(maxRequests = 5, timeWindowMs = 60000)
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        return ResponseEntity.ok(ApiResponse.success(authService.refreshAccessToken(request)));
    }
    @PostMapping("/reset-password")
    @RateLimit(maxRequests = 5, timeWindowMs = 30000)
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO request){
        return ResponseEntity.ok(ApiResponse.success(authService.resetPassword(request)));
    }
    @PostMapping("/resend-code")
    @RateLimit(maxRequests = 1, timeWindowMs = 1500)
    public ResponseEntity<?> resendCode(@Valid @RequestBody ResetVerificationCodeDTO request){
        return ResponseEntity.ok(ApiResponse.success(authService.resendVerificationCode(request)));
    }
}