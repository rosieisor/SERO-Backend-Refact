package com.werp.sero.file.service;

import com.werp.sero.file.dto.PresignedUrlRequest;
import com.werp.sero.file.dto.PresignedUrlResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    PresignedUrlResponse generatePresignedUploadUrl(final PresignedUrlRequest request);

    String uploadObject(final String objectPath, final MultipartFile file);

    String copyObject(final String targetPath, final String url);

    String putByteArrayObject(final String objectPath, final byte[] bytes, final String originalFileName,
                              final String contentType);

    void deleteObject(final String url);
}