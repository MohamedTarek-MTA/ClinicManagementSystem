package com.IBM.ClinicManagementSystem.Repositories.Mysql;

import com.IBM.ClinicManagementSystem.Models.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
}
