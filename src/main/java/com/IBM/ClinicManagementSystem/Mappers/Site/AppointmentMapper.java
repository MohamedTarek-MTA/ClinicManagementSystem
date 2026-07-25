package com.IBM.ClinicManagementSystem.Mappers.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.AppointmentDTO;
import com.IBM.ClinicManagementSystem.Models.Entities.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentDTO toDTO(Appointment appointment);

    Appointment toEntity(AppointmentDTO appointmentDTO);
}
