package com.IBM.ClinicManagementSystem.Services.Site;

import com.IBM.ClinicManagementSystem.Models.Documents.Feedback;
import com.IBM.ClinicManagementSystem.Repositories.MongoDB.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public Page<Feedback> getAllFeedbacks(Pageable pageable){
        return feedbackRepository.findAll(pageable);
    }

    public Page<Feedback> getAllByUserId(Long userId,Pageable pageable){
        return feedbackRepository.findByUserId(userId,pageable);
    }

    public Feedback getById(String id){
        return feedbackRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Not Found!"));
    }

    public Feedback createFeedback(Long userId,String message){
        var feedback = Feedback.builder()
                .userId(userId)
                .message(message)
                .build();
        return feedbackRepository.save(feedback);
    }
    public Feedback replyOnFeedback(String id,String reply,Feedback.Status status){
        var feedback = feedbackRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Not Found!"));
        feedback.setStatus(status);
        feedback.setReply(reply);
        return feedbackRepository.save(feedback);
    }

    public void deleteFeedbackById(String id){
         feedbackRepository.deleteById(id);
    }
}
