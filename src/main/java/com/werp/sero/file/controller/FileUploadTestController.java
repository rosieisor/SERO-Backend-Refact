package com.werp.sero.file.controller;

import com.werp.sero.file.service.FileUploader;
import com.werp.sero.file.service.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "File Upload Test", description = "파일 업로드 테스트 API")
@RestController
@RequestMapping("/test/files")
@RequiredArgsConstructor
public class FileUploadTestController {
    private final FileUploader fileUploader;
    private final FileValidator fileValidator;

    @Operation(summary = "이미지 파일 업로드 테스트 (단일/다중)", description = "이미지 파일(들)을 S3에 업로드하고 URL을 반환.")
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("files") List<MultipartFile> files) {

        List<Map<String, String>> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            // 이미지 파일 검증
            fileValidator.validateImage(file);

            String fileUrl = fileUploader.uploadObject("sero/images/", file);

            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("originalFileName", file.getOriginalFilename());
            fileInfo.put("fileUrl", fileUrl);
            fileInfo.put("contentType", file.getContentType());

            uploadedFiles.add(fileInfo);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", uploadedFiles.size());
        response.put("files", uploadedFiles);
        response.put("message", uploadedFiles.size() + "개 파일 업로드 성공!");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "문서 파일 업로드 테스트 (단일/다중)", description = "문서 파일(들)을 S3에 업로드하고 URL을 반환. 지원 형식: PDF, 한글(HWP), MS Word(doc/docx), MS Excel(xls/xlsx)")
    @PostMapping("/upload/document")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("files") List<MultipartFile> files) {

        List<Map<String, String>> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            // 문서 파일 검증
            fileValidator.validateDocument(file);

            String fileUrl = fileUploader.uploadObject("sero/documents/", file);

            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("originalFileName", file.getOriginalFilename());
            fileInfo.put("fileUrl", fileUrl);
            fileInfo.put("contentType", file.getContentType());

            uploadedFiles.add(fileInfo);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", uploadedFiles.size());
        response.put("files", uploadedFiles);
        response.put("message", uploadedFiles.size() + "개 파일 업로드 성공!");

        return ResponseEntity.ok(response);
    }

    @Deprecated
    @Operation(summary = "[Deprecated] PDF 파일 업로드 테스트", description = "deprecated - /upload/document 사용 권장")
    @PostMapping("/upload/pdf")
    public ResponseEntity<Map<String, Object>> uploadPdf(
            @RequestParam("files") List<MultipartFile> files) {
        return uploadDocument(files);
    }
}
