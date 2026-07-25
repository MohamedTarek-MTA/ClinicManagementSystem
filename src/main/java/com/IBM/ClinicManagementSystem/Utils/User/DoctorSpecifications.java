package com.IBM.ClinicManagementSystem.Utils.User;

import com.IBM.ClinicManagementSystem.Models.Entities.Doctor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DoctorSpecifications {
    public static Specification<Doctor> filterDoctors(String name,String specialization){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Filter by name (case-insensitive substring)
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            // Filter by address (case-insensitive substring)
            if (StringUtils.hasText(specialization)) {
                predicates.add(cb.like(cb.lower(root.get("specialization")), "%" + specialization.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
