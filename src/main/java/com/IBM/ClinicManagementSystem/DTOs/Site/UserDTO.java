package com.IBM.ClinicManagementSystem.DTOs.Site;

import com.IBM.ClinicManagementSystem.Models.Entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String age;
    private String address;
    private String info;
    private String profileImageUrl;
    private User.Gender gender;
    private User.Role role;
    private User.Status status;
    private LocalDate birthdate;
    private LocalDateTime createdAt;
    private Boolean enabled;
}
