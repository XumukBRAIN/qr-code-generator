package com.dev.qr_code_generator.services;

import com.dev.qr_code_generator.configurations.minio.MinioClientConfig;
import io.micrometer.common.util.StringUtils;
import io.minio.*;
import io.minio.http.Method;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
public class MinioService {

    public void uploadFile(MultipartFile file, String bucketName) {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            throw new RuntimeException("MinioClient не доступен");
        }

        String filename = file.getName();
        try {
            InputStream inputStream = file.getInputStream();
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(file.getOriginalFilename())
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла с названием {}: {}", filename, e.getMessage());
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }

    public InputStream downloadFile(String bucketName, String filename, HttpServletResponse response) {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            throw new RuntimeException("MinioClient не доступен");
        }

        if (StringUtils.isBlank(filename)) {
            throw new RuntimeException("Отсутствует имя файла");
        }

        try {
            InputStream file = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );

            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ServletOutputStream servletOutputStream = response.getOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = file.read(buffer)) > 0) {
                servletOutputStream.write(buffer, 0, len);
            }
            servletOutputStream.flush();
            file.close();
            servletOutputStream.close();

            log.info("Файл с названием {} успешно скачен", filename);
            return file;
        } catch (Exception e) {
            log.error("Ошибка при скачивании файла с названием {}: {}", filename, e.getMessage());
            throw new RuntimeException("Ошибка при скачивании файла");
        }
    }

    public String getPreviewFileUrl(String bucketName, String filename) {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            throw new RuntimeException("MinioClient не доступен");
        }

        if (StringUtils.isBlank(filename)) {
            throw new RuntimeException("Отсутствует имя файла");
        }

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            log.error("Ошибка при получении URL файла с названием {}: {}", filename, e.getMessage());
            throw new RuntimeException("Ошибка при получении URL файла", e);
        }
    }

    public void deleteFile(String bucketName, String filename) {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            throw new RuntimeException("MinioClient не доступен");
        }

        if (StringUtils.isBlank(filename)) {
            throw new RuntimeException("Отсутствует имя файла");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );

            log.info("Файл с названием {} успешно удален", filename);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла с названием {}: {}", filename, e.getMessage());
            throw new RuntimeException("Ошибка при удалении файла");
        }
    }
}
