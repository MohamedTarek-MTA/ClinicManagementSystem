package com.IBM.ClinicManagementSystem.Models.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime appointmentDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;

    public enum AppointmentStatus{
        SCHEDULED,CANCELED,COMPLETED
    }
    @PrePersist
    protected void onCreate(){
        this.appointmentDate = LocalDateTime.now();
        this.appointmentStatus = AppointmentStatus.SCHEDULED;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id",referencedColumnName = "id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id",referencedColumnName = "id")
    private Doctor doctor;
}
