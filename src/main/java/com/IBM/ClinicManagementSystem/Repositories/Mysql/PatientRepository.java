package com.IBM.ClinicManagementSystem.Repositories.Mysql;

import com.IBM.ClinicManagementSystem.Models.Entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient,Long> {
}
