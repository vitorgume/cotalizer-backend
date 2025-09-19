package com.gumeinteligenciacomercial.orcaja.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.s3")
public record S3Props(String bucket, String region, String endpoint,
                      String accessKey, String secretKey) {}
