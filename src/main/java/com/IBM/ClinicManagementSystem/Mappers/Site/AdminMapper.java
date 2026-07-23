package com.IBM.ClinicManagementSystem.Mappers.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.AdminDTO;
import com.IBM.ClinicManagementSystem.Mappers.Image.ImageMapper;
import com.IBM.ClinicManagementSystem.Models.Entities.Admin;
import com.IBM.ClinicManagementSystem.Utils.Helper.Helper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring",uses = ImageMapper.class)
public interface AdminMapper {
    @Mapping(target = "age", source = "birthdate", qualifiedByName = "calculateAge")
    @Mapping(target = "profileImageUrl",
            source = "profileImageKey",
            qualifiedByName = "keyToUrl")
    AdminDTO toDTO(Admin admin);

    Admin toEntity(AdminDTO adminDTO);
    @Named("calculateAge")
    default String calculateAge(LocalDate birthdate) {
        return birthdate == null ? null : Helper.getAge(birthdate);
    }

}
