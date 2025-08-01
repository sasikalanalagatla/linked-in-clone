package com.org.linkedin.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.org.linkedin.configuration.CloudinaryConfigProperty;
import com.org.linkedin.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final CloudinaryConfigProperty cloudinaryConfigProperty;

    public CloudinaryService(CloudinaryConfigProperty cloudinaryConfigProperty) {
        this.cloudinaryConfigProperty = cloudinaryConfigProperty;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomException("INVALID_FILE", "File cannot be null or empty");
        }

        Map uploadResult = getCloudinary().uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "raw"
        ));

        if (uploadResult == null || uploadResult.get("secure_url") == null) {
            throw new CustomException("UPLOAD_FAILED", "Failed to upload file to Cloudinary");
        }

        return uploadResult.get("secure_url").toString();
    }

    private Cloudinary getCloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", cloudinaryConfigProperty.getCloudName(),
                "api_key", cloudinaryConfigProperty.getApiKey(),
                "api_secret", cloudinaryConfigProperty.getApiSecret()
        );
        return new Cloudinary(config);
    }
}