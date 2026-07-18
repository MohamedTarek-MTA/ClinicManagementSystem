package com.IBM.ClinicManagementSystem.DTOs;

import com.IBM.ClinicManagementSystem.Models.Entities.Appointment;
import com.IBM.ClinicManagementSystem.Models.Entities.Doctor;
import com.IBM.ClinicManagementSystem.Models.Entities.Patient;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {
    private Long id;
    private Appointment.AppointmentStatus appointmentStatus;
    private LocalDateTime appointmentDate;
    private Patient patient;
    private Doctor doctor;
}
