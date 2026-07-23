package com.IBM.ClinicManagementSystem.Models.Documents;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "feedbacks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {
    @Id
    private String id;
    @NotNull(message = "User ID is required")
    @Indexed
    private Long userId;
    @NotBlank(message = "Message is required")
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    @NotBlank(message = "Reply message is required")
    private String reply;
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    public enum Status{
        NEW,DECLINED,COMPLETED
    }
}
