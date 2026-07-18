package com.IBM.ClinicManagementSystem.Models.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Patient extends User{
    @OneToMany(mappedBy = "patient",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
}
