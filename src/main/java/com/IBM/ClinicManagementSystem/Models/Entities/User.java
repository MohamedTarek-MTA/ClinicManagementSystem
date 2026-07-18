package com.IBM.ClinicManagementSystem.Models.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users",indexes = {
        @Index(name = "idx_user_name", columnList = "name"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_phone", columnList = "phone")
})

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^(\\+20|0)1[0125]\\d{8}$",
            message = "Invalid phone number")
    @Column(unique = true)
    private String phone;
    @NotBlank(message = "Email is required")
    @Email
    @Column(unique = true,nullable = false)
    private String email;
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#^_+=()-])[A-Za-z\\d@$!%*?&.#^_+=()-]{8,}$",
            message = "Password must be at least 8 characters and contain an uppercase letter, a lowercase letter, a number, and a special character"
    )
    private String password;
    private String profileImageUrl;
    private String address;
    @NotNull
    private LocalDate birthdate;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
    private String verificationCode;
    private String info;
    private Boolean enabled;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+role.name().toUpperCase()));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled != null && enabled; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.enabled == null) this.enabled = false;
    }
    public enum Gender{
        MALE,FEMALE,OTHER
    }
    public enum Role{
        ADMIN,PATIENT,DOCTOR,OTHER
    }
    public enum Status{
        ACTIVE,BANNED,DELETED
    }

}
