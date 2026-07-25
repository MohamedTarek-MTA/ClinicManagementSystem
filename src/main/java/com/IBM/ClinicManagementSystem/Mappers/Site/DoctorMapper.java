package com.IBM.ClinicManagementSystem.Mappers.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.DoctorDTO;
import com.IBM.ClinicManagementSystem.Mappers.Image.ImageMapper;
import com.IBM.ClinicManagementSystem.Models.Entities.Doctor;
import com.IBM.ClinicManagementSystem.Utils.Helper.Helper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring",uses = ImageMapper.class)
public interface DoctorMapper {
    @Mapping(target = "age", source = "birthdate", qualifiedByName = "calculateAge")
    @Mapping(target = "profileImageUrl",
            source = "profileImageKey",
            qualifiedByName = "keyToUrl")
    DoctorDTO toDTO(Doctor doctor);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "verificationCode", ignore = true)
    @Mapping(target = "verificationCodeExpirationTime",ignore = true)
    @Mapping(target = "profileImageKey", ignore = true)
    Doctor toEntity(DoctorDTO doctorDTO);

    @Named("calculateAge")
    default String calculateAge(LocalDate birthdate) {
        return birthdate == null ? null : Helper.getAge(birthdate);
    }

}
