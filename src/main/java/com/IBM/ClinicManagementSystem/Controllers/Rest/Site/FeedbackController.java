package com.IBM.ClinicManagementSystem.Controllers.Rest.Site;

import com.IBM.ClinicManagementSystem.Models.Documents.Feedback;
import com.IBM.ClinicManagementSystem.Services.Security.CustomUserDetails;
import com.IBM.ClinicManagementSystem.Services.Site.FeedbackService;
import com.IBM.ClinicManagementSystem.Utils.Helper.ApiResponse;
import com.IBM.ClinicManagementSystem.Utils.Helper.Helper;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Feedback>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        Pageable pageable = Helper.pageHandler(page,size,sortBy,direction);
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getAllFeedbacks(pageable)));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Feedback>>> getAllByUserId(@PathVariable Long id,
                                                                      @RequestParam(defaultValue = "0") @Min(0) int page,
                                                                      @RequestParam(defaultValue = "10") @Min(1) int size,
                                                                      @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                      @RequestParam(defaultValue = "asc") String direction){
        Pageable pageable = Helper.pageHandler(page,size,sortBy,direction);
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getAllByUserId(id,pageable)));
    }

    @GetMapping("/my-feedbacks")
    public ResponseEntity<ApiResponse<Page<Feedback>>> getMyFeedbacks(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(defaultValue = "0") @Min(0) int page,
                                                                    @RequestParam(defaultValue = "10") @Min(1) int size,
                                                                    @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                    @RequestParam(defaultValue = "asc") String direction){
        Pageable pageable = Helper.pageHandler(page,size,sortBy,direction);
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getAllByUserId(userDetails.getId(),pageable)));
    }

    @PostMapping("/my-feedbacks")
    public ResponseEntity<ApiResponse<Feedback>> createFeedback(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody(required = true) String message){
        return ResponseEntity.ok(ApiResponse.created("Created!",feedbackService.createFeedback(userDetails.getId(),message)));
    }
    @PatchMapping("/reply/feedback/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Feedback>> replyOnFeedback(@PathVariable String id,@RequestBody(required = true) String reply,@RequestParam Feedback.Status status){
        return ResponseEntity.ok(ApiResponse.success(feedbackService.replyOnFeedback(id,reply,status)));
    }
}
