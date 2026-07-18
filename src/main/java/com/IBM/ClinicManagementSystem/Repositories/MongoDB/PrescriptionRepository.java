package com.IBM.ClinicManagementSystem.Repositories.MongoDB;

import com.IBM.ClinicManagementSystem.Models.Documents.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrescriptionRepository extends MongoRepository<Prescription,String> {
    Page<Prescription> findByPatientId(Long patientId, Pageable pageable);

    Page<Prescription> findByDoctorId(Long doctorId,Pageable pageable);

    Page<Prescription> findByAppointmentId(Long appointmentId, Pageable pageable);
}
