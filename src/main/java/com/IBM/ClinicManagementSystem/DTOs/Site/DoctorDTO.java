package com.IBM.ClinicManagementSystem.DTOs.Site;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DoctorDTO extends UserDTO{
    private String specialization;
    private String clinicInfo;
}
