package com.gumeinteligenciacomercial.orcaja.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Slf4j
public class S3Config {

    @Value("${cotalizer.s3.region}")
    private String region;

//    @Bean
//    public S3Client s3Client() {
//        return S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(DefaultCredentialsProvider.create()) // usa IAM role do App Runner
//                .build();
//    }

    @Bean
    S3Client s3Client(
            @Value("${cotalizer.s3.region}") String region,
            @Value("${cotalizer.s3.profile}") String profile
    ) {
        S3ClientBuilder builder = S3Client.builder().region(Region.of(region));

        AwsCredentialsProvider provider;
        if (profile != null && !profile.isBlank()) {
            provider = ProfileCredentialsProvider.builder()
                    .profileName(profile)
                    .build();                  // usa ~/.aws/credentials: [dev]
        } else {
            provider = DefaultCredentialsProvider.create(); // App Runner usa IAM Role
        }

        // (opcional) LOG: mostra qual AccessKey está sendo usada (só o prefixo)
        AwsCredentials creds = provider.resolveCredentials();
        log.info("AWS profile em uso: {} | AKID: {}***",
                (profile == null || profile.isBlank()) ? "default/role" : profile,
                creds.accessKeyId().substring(0, 4));

        return builder.credentialsProvider(provider).build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
