package com.IBM.ClinicManagementSystem.Services.Site;

import com.IBM.ClinicManagementSystem.Repositories.Mysql.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    
}
