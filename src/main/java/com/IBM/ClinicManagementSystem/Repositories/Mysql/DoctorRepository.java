package com.IBM.ClinicManagementSystem.Repositories.Mysql;

import com.IBM.ClinicManagementSystem.Models.Entities.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);

    Page<Doctor> findByNameContainingIgnoreCase(String name,Pageable pageable);


}
