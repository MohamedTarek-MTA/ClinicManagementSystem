package com.IBM.ClinicManagementSystem.Repositories.Mysql;

import com.IBM.ClinicManagementSystem.Models.Entities.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    @EntityGraph(attributePaths = {"doctor","patient"})
    Page<Appointment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"doctor","patient"})
    Page<Appointment> findByAppointmentStatus(Appointment.AppointmentStatus appointmentStatus, Pageable pageable);

    @EntityGraph(attributePaths = {"doctor","patient"})
    Page<Appointment> findByPatient_Id(Long patientId,Pageable pageable);
    @EntityGraph(attributePaths = {"doctor","patient"})
    Page<Appointment> findByDoctor_Id(Long doctorId,Pageable pageable);
}
