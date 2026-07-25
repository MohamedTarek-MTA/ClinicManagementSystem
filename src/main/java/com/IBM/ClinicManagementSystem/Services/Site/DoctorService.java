package com.IBM.ClinicManagementSystem.Services.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.DoctorDTO;
import com.IBM.ClinicManagementSystem.DTOs.Site.PageDTO;
import com.IBM.ClinicManagementSystem.Mappers.Site.DoctorMapper;
import com.IBM.ClinicManagementSystem.Mappers.Site.PageMapper;
import com.IBM.ClinicManagementSystem.Models.Entities.Doctor;
import com.IBM.ClinicManagementSystem.Repositories.Mysql.DoctorRepository;
import com.IBM.ClinicManagementSystem.Utils.User.DoctorSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Cacheable(
            value = "doctorsPage",
            key = "{" +
                    "#name, " +
                    "#specialization,"+
                    "#pageable.pageNumber, " +
                    "#pageable.pageSize, " +
                    "#pageable.sort.toString()" +
                    "}"
    )
    public PageDTO<DoctorDTO> searchDoctors(
            String name,
            String specialization,
            Pageable pageable
    ) {
        Specification<Doctor> specification =
                DoctorSpecifications.filterDoctors(name, specialization);
        return PageMapper.toDTO(
                doctorRepository.findAll(specification, pageable).map(doctorMapper::toDTO));
    }

    @Cacheable(value = "doctorsById",key = "#id")
    public DoctorDTO getById(Long id){
        Doctor doctor = doctorRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Doctor Not Found!"));

        return doctorMapper.toDTO(doctor);
    }
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "doctorsPage", allEntries = true),
                    @CacheEvict(value = "doctorsById", key = "#result.id", condition = "#result != null")
            }
    )
    public DoctorDTO updateDoctorDetails(Long id,DoctorDTO doctorDTO){
        var existsDoctor = doctorRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Doctor Not Found"));
        Optional.ofNullable(doctorDTO.getSpecialization()).ifPresent(existsDoctor::setSpecialization);
        Optional.ofNullable(doctorDTO.getClinicInfo()).ifPresent(existsDoctor::setClinicInfo);
        return doctorMapper.toDTO(doctorRepository.saveAndFlush(existsDoctor));
    }

}
