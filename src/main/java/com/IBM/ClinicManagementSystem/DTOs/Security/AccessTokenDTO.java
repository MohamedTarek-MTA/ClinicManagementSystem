package com.IBM.ClinicManagementSystem.DTOs.Security;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessTokenDTO {
    private String accessToken;
}
