package com.werp.sero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Profile("s3")
public class S3Config {
    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(staticCredentialsProvider())
                .region(Region.of(region))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .credentialsProvider(staticCredentialsProvider())
                .region(Region.of(region))
                .build();
    }

    // StaticCredentialsProvider: 환경변수나 임시 토큰 등 여러 인증 방식 중 아래에서 만든 고정된(Static) 열쇠를 사용하겠다고 S3 클라이언트에게 명시
    private StaticCredentialsProvider staticCredentialsProvider() {
        // AwsBasicCredentials: Access Key와 Secret Key를 AWS가 인식할 수 있는 하나의 기본 자격 증명 캡슐로 포장
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }
}