package com.IBM.ClinicManagementSystem.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionDTO implements Serializable {
    private String id;

    @NotNull(message = "Patient ID is required")
    @Indexed
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    private String diagnosis;

    private List<String> prescriptions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
