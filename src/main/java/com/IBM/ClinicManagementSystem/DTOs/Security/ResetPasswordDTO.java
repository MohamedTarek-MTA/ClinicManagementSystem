package com.IBM.ClinicManagementSystem.DTOs.Security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ResetPasswordDTO implements Serializable {
    @NotBlank
    @Email(message = "Please insert valid email !")
    private String email;
    @NotBlank
    @Pattern(regexp = "^(\\+20|0)1[0125]\\d{8}$",message = "Please insert valid phone number !")
    private String phone;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*\\d)" +
                    "(?=.*[@$!%*?&])" +
                    "[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String newPassword;
    @NotBlank
    private String confirmedNewPassword;
}
