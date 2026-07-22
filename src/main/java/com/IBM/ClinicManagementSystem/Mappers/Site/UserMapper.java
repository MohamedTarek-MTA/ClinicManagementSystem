package com.IBM.ClinicManagementSystem.Mappers.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.UserDTO;
import com.IBM.ClinicManagementSystem.Mappers.Image.ImageMapper;
import com.IBM.ClinicManagementSystem.Models.Entities.User;
import com.IBM.ClinicManagementSystem.Utils.Helper.Helper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring", uses = ImageMapper.class)
public interface UserMapper {


    @Mapping(target = "age", source = "birthdate", qualifiedByName = "calculateAge")
    @Mapping(target = "profileImageUrl",
            source = "profileImageKey",
            qualifiedByName = "keyToUrl")
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "verificationCode", ignore = true)
    @Mapping(target = "verificationCodeExpirationTime",ignore = true)
    @Mapping(target = "profileImageKey", ignore = true)
    User toEntity(UserDTO userDTO);

    @Named("calculateAge")
    default String calculateAge(LocalDate birthdate) {
        return birthdate == null ? null : Helper.getAge(birthdate);
    }
}
