package com.IBM.ClinicManagementSystem.Utils.User;


import com.IBM.ClinicManagementSystem.Models.Entities.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {

    public static Specification<User> filterUsers(
            String name,
            String address,
            User.Gender gender,
            User.Status status,
            User.Role role
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by name (case-insensitive substring)
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            // Filter by address (case-insensitive substring)
            if (StringUtils.hasText(address)) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%"));
            }

            // Filter by gender (exact match)
            if (gender != null) {
                predicates.add(cb.equal(root.get("gender"), gender));
            }

            // Filter by status (exact match)
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            // Filter by status (exact match)
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            // Combine all active predicates with AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
