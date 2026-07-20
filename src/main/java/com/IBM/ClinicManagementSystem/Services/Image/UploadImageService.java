package com.IBM.ClinicManagementSystem.Services.Image;

import com.IBM.ClinicManagementSystem.Exceptions.ImageUploadException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadImageService {
    private final RestTemplate restTemplate;
    @Value("${imgbb.api.key}")
    private String imgbbApiKey;
    private final ObjectMapper objectMapper ;

    private String uploadBase64Image(String base64Image){

        String uploadUrl = "https://api.imgbb.com/1/upload?key="+imgbbApiKey;

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("image",base64Image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String,String>> request= new HttpEntity<>(body,headers);
        try {
            ResponseEntity<Map<String,Object>> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>(){}

            );
            if(response.getStatusCode() == HttpStatus.OK && !response.getBody().isEmpty()){
                JsonNode jsonNode = objectMapper.convertValue(response.getBody(), JsonNode.class);
                String imageUrl = jsonNode.path("data").path("url").asText();
                if(!StringUtils.hasText(imageUrl)){
                    throw new ImageUploadException("Invalid response: missing image URL");
                }
                return imageUrl;

            }
            else{
                throw new ImageUploadException("Failed to upload image. Status: " + response.getStatusCode());
            }
        }
        catch (Exception e) {
            throw new ImageUploadException("Failed to upload image", e);
        }
    }
    public String uploadMultipartFile(MultipartFile file){
        try {
            byte[] bytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            return uploadBase64Image(base64Image);
        }
        catch (IOException e){
            throw new ImageUploadException("Failed to read image file ",e);
        }
    }
    public  String generateImageUrl(MultipartFile image){
        if(image != null && !image.isEmpty()){
            try {
                return uploadMultipartFile(image);
            }
            catch (Exception e){
                throw new ImageUploadException("The Image Uploading Process Failed !",e.getCause());
            }
        }
        return null;
    }
}
