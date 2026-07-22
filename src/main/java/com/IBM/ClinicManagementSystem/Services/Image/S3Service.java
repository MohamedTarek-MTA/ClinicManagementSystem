package com.IBM.ClinicManagementSystem.Services.Image;

import com.IBM.ClinicManagementSystem.Exceptions.ImageUploadException;
import com.IBM.ClinicManagementSystem.Repositories.AWS.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements S3Repository {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Override
    public String uploadImage(MultipartFile image, String folder) {

        validate(image);

        try {

            String extension = getExtension(image);

            String key = folder + "/" + UUID.randomUUID() + "." + extension;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(image.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(image.getBytes())
            );

            return key;

        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image.", e);
        }
    }

    @Override
    public void deleteImage(String key) {

        if (!StringUtils.hasText(key)) {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    @Override
    public String getImageUrl(String key) {

        if (!StringUtils.hasText(key))
            return null;

        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                key
        );
    }

    private void validate(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new ImageUploadException("Image is required.");
        }

        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new ImageUploadException("Unsupported image type.");
        }

        long maxSize = 5 * 1024 * 1024;

        if (image.getSize() > maxSize) {
            throw new ImageUploadException("Maximum image size is 5MB.");
        }
    }

    private String getExtension(MultipartFile image) {

        String filename = image.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw new ImageUploadException("Invalid file.");
        }

        return filename.substring(filename.lastIndexOf('.') + 1);
    }

}