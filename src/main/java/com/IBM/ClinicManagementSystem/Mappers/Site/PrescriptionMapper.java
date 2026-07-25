package com.IBM.ClinicManagementSystem.Mappers.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.PrescriptionDTO;
import com.IBM.ClinicManagementSystem.Models.Documents.Prescription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {
    PrescriptionDTO toDTO(Prescription prescription);
    Prescription toEntity(PrescriptionDTO prescriptionDTO);
}
