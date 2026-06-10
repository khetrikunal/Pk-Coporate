package com.pkcorporate.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, String> uploadFile(MultipartFile file, String folder) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "pk-corporate/" + folder,
                        "resource_type", "auto",
                        "transformation", "q_auto,f_auto"
                ));

        return Map.of(
                "url", (String) result.get("secure_url"),
                "publicId", (String) result.get("public_id")
        );
    }

    public Map<String, String> uploadImage(MultipartFile file, String folder) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "pk-corporate/" + folder,
                        "resource_type", "image",
                        "transformation", "q_auto,f_webp,w_1200"
                ));

        return Map.of(
                "url", (String) result.get("secure_url"),
                "publicId", (String) result.get("public_id")
        );
    }

    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("Failed to delete file from Cloudinary: {}", publicId);
        }
    }
}
