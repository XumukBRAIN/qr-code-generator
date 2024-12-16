package com.dev.qr_code_generator.configurations.minio;

import io.minio.MinioClient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioClientConfig {

    StorageProperties storageProperties;

    @Getter
    private static MinioClient minioClient;

    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials(storageProperties.getAccessKey(), storageProperties.getSecretKey())
                    .build();
        } catch (Exception e) {
            log.error("Initiating Minio Configuration Anomalous: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
