package com.IBM.ClinicManagementSystem.DTOs.Mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailDTO {
    @NotBlank
    @Email(message = "Please insert valid email")
    private String email;
    @NotBlank
    private String verificationCode;
    @JsonIgnore
    private LocalDateTime verificationCodeExpirationTime;
}
