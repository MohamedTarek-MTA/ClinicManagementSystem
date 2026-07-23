package com.IBM.ClinicManagementSystem.Services.Site;

import com.IBM.ClinicManagementSystem.Repositories.Mysql.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;


}
