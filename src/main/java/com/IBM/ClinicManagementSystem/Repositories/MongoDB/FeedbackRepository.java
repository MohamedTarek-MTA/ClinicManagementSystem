package com.IBM.ClinicManagementSystem.Repositories.MongoDB;

import com.IBM.ClinicManagementSystem.Models.Documents.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FeedbackRepository extends MongoRepository<Feedback,String> {

    Page<Feedback> findByUserId(Long userId, Pageable pageable);
}
