package com.IBM.ClinicManagementSystem.Repositories.AWS;

import org.springframework.web.multipart.MultipartFile;

public interface S3Repository {

    String uploadImage(MultipartFile image, String folder);

    void deleteImage(String key);

    String getImageUrl(String key);

}