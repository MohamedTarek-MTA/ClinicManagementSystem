package com.IBM.ClinicManagementSystem.Models.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Doctor extends User{

    @NotBlank(message = "Please insert specialization")
    private String specialization;
    @NotBlank(message = "Please insert clinic info")
    private String clinicInfo;

    @OneToMany(mappedBy = "doctor",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
}
