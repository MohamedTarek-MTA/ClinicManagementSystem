package com.IBM.ClinicManagementSystem.DTOs.Security;

import com.IBM.ClinicManagementSystem.Models.Entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDTO implements Serializable {
    @NotBlank(message = "First Name is required !")
    private String firstName;
    @NotBlank(message = "Last Name is required !")
    private String lastName;
    @NotBlank(message = "First Name is required !")
    @Email(message = "Please insert valid email !")
    private String email;
    @NotBlank(message = "Password is required !")
    @Pattern(
            regexp = "^(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*\\d)" +
                    "(?=.*[@$!%*?&])" +
                    "[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String password;
    @NotBlank(message = "Phone Number is required !")
    @Pattern(regexp = "^(\\+20|0)1[0125]\\d{8}$", message = "Please insert valid phone number !")
    private String phone;
    private String address;

    @NotNull(message = "Gender is required !")
    private User.Gender gender;
    @NotNull(message = "Role is required !")
    private User.Role role;

    @NotNull(message = "Birthdate is required !")
    private LocalDate birthdate;
}
