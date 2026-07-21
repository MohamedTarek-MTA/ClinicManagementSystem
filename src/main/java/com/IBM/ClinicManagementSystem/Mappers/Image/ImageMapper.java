package com.IBM.ClinicManagementSystem.Mappers.Image;

import com.IBM.ClinicManagementSystem.Services.Image.S3Service;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageMapper {

    private final S3Service s3Service;

    @Named("keyToUrl")
    public String keyToUrl(String key) {
        return s3Service.getImageUrl(key);
    }
}