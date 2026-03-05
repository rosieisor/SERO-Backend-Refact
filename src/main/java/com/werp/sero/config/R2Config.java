package com.werp.sero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@Profile("r2")
public class R2Config {
    @Value("${cloudflare.r2.account-id}")
    private String accountId;

    @Value("${cloudflare.r2.access-key}")
    private String accessKey;

    @Value("${cloudflare.r2.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(generateEndPointURI())
                .credentialsProvider(staticCredentialsProvider())
                .region(Region.of("auto"))
                // AWS S3가 아닌 R2(호환 스토리지)를 사용하기 위한 필수 호환성 설정
                .serviceConfiguration(S3Configuration.builder()
                        // URL 주소에 버킷 이름을 어디에 배치할 것인지 여부
                        // Virtual Hosted-Style: https://[버킷이름].[엔드포인트]/[파일경로] (AWS S3 기본값)
                        // Path-Style: https://[엔드포인트]/[버킷이름]/[파일경로] (true 설정 시 적용)
                        .pathStyleAccessEnabled(true)
                        // AWS SDK v2의 aws-chunked 방식(청크별 서명 포함) 업로드를 비활성화하는 설정
                        // R2는 AWS 고유의 chunked signing 방식을 완벽히 지원하지 않으므로
                        // Content-Length를 명시하는 일반 업로드 방식을 사용하도록 false 설정
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(generateEndPointURI())
                .credentialsProvider(staticCredentialsProvider())
                .region(Region.of("auto"))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    private StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    private URI generateEndPointURI() {
        return URI.create("https://" + accountId + ".r2.cloudflarestorage.com");
    }
}