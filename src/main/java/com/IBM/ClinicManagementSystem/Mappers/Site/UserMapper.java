package com.IBM.ClinicManagementSystem.Mappers.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.UserDTO;
import com.IBM.ClinicManagementSystem.Models.Entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "age", source = "birthdate", qualifiedByName = "calculateAge")
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "verificationCode", ignore = true)
    User toEntity(UserDTO userDTO);

    @Named("calculateAge")
    default String calculateAge(LocalDate birthdate) {
        if (birthdate == null) {
            return null;
        }
        return String.valueOf(Period.between(birthdate, LocalDate.now()).getYears());
    }
}
