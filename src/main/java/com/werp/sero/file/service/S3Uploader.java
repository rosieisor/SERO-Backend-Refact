package com.werp.sero.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Profile("s3")
@Service
public class S3Uploader extends AbstractFileUploader {
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public S3Uploader(final S3Client s3Client, final S3Presigner s3Presigner) {
        super(s3Client, s3Presigner);
    }

    @Override
    protected String getBucket() {
        return bucket;
    }

    @Override
    protected String generatePublicUrl(final String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}