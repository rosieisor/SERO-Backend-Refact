package com.werp.sero.file.controller;

import com.werp.sero.file.service.FileUploader;
import com.werp.sero.file.dto.PresignedUrlRequest;
import com.werp.sero.file.dto.PresignedUrlResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "파일", description = "파일 관련 API")
@RequestMapping("/files")
@RequiredArgsConstructor
@RestController
public class FileController {
    private final FileUploader fileUploader;

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUploadUrl(@Valid @RequestBody final PresignedUrlRequest request) {
        // TODO Content-Type 검증 로직 추가
        return ResponseEntity.ok(fileUploader.generatePresignedUploadUrl(request));
    }
}