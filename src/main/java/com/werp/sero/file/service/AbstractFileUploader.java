package com.werp.sero.file.service;

import com.werp.sero.common.error.ErrorCode;
import com.werp.sero.common.error.exception.BusinessException;
import com.werp.sero.common.error.exception.SystemException;
import com.werp.sero.file.dto.PresignedUrlRequest;
import com.werp.sero.file.dto.PresignedUrlResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.UUID;

// 파일 업로더 공통 로직 모아둔 추상 클래스
public abstract class AbstractFileUploader implements FileUploader {
    protected final S3Client s3Client;
    protected final S3Presigner s3Presigner;

    AbstractFileUploader(final S3Client s3Client, final S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    protected abstract String getBucket();

    protected abstract String generatePublicUrl(String key);

    @Override
    public PresignedUrlResponse generatePresignedUploadUrl(final PresignedUrlRequest request) {
        final String key = generateKey("temp/", request.getFileName());

        final PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(getBucket())
                .key(key)
                .contentType(request.getContentType())
                .build();

        final PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        return new PresignedUrlResponse(s3Presigner.presignPutObject(presignRequest).url().toExternalForm(), key);
    }

    @Override
    public String uploadObject(final String objectPath, final MultipartFile file) {
        try {
            final String key = generateKey(objectPath, file.getOriginalFilename());

            final PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return generatePublicUrl(key);
        } catch (S3Exception | IOException e) {
            throw new SystemException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    @Override
    public String copyObject(final String targetPath, final String url) {
        final String objectKey = extractKey(url);
        final String destinationKey = targetPath + objectKey.substring(objectKey.lastIndexOf("/") + 1);

        final CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(getBucket())
                .sourceKey(objectKey)
                .destinationBucket(getBucket())
                .destinationKey(destinationKey)
                .build();
        try {
            s3Client.copyObject(copyRequest);

            return generatePublicUrl(destinationKey);
        } catch (S3Exception e) {
            throw new SystemException(ErrorCode.S3_COPY_FAILED);
        }
    }

    @Override
    public String putByteArrayObject(final String objectPath, final byte[] bytes, final String originalFileName,
                                     final String contentType) {
        try {
            final String key = generateKey(objectPath, originalFileName);

            final PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(getBucket())
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));

            return generatePublicUrl(key);
        } catch (S3Exception e) {
            throw new SystemException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    @Override
    public void deleteObject(final String url) {
        try {
            final String key = extractKey(url);

            final DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(getBucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (S3Exception e) {
            throw new SystemException(ErrorCode.S3_DELETE_FAILED);
        }
    }

    private String generateKey(final String path, final String originalFilename) {
        final String extension = StringUtils.getFilenameExtension(originalFilename);

        return path + UUID.randomUUID() + "." + extension;
    }

    private String extractKey(final String url) {
        try {
            final URI uri = new URI(url);

            return uri.getPath().substring(1);
        } catch (URISyntaxException e) {
            throw new BusinessException(ErrorCode.S3_URL_INVALID);
        }
    }
}