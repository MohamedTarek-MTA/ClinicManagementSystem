package com.IBM.ClinicManagementSystem.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
}
