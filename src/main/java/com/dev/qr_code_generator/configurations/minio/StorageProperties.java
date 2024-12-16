package com.dev.qr_code_generator.configurations.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "s3")
public class StorageProperties {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
