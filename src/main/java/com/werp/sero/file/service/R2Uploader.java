package com.werp.sero.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Profile("r2")
@Service
public class R2Uploader extends AbstractFileUploader {
    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.account-id}")
    private String accountId;

    public R2Uploader(final S3Client s3Client, final S3Presigner s3Presigner) {
        super(s3Client, s3Presigner);
    }

    @Override
    protected String getBucket() {
        return bucket;
    }

    @Override
    protected String generatePublicUrl(final String key) {
        return "https://" + accountId + ".r2.cloudflarestorage.com/" + key;
    }
}