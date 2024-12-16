package com.dev.qr_code_generator.controllers;

import com.dev.qr_code_generator.configurations.minio.StorageProperties;
import com.dev.qr_code_generator.services.MinioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/minio")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioController {
    
    MinioService minioUtil;
    StorageProperties properties;

    @PostMapping("/uploadFile")
    public ResponseEntity<Integer> uploadFile(@RequestParam("file") MultipartFile file) {
        minioUtil.uploadFile(file, properties.getBucketName());
        return ResponseEntity.ok(HttpStatus.OK.value());
    }

    @GetMapping("/getFile")
    public ResponseEntity<String> getFile(@RequestParam String filename) {
        return ResponseEntity.ok(minioUtil.getPreviewFileUrl(properties.getBucketName(), filename));
    }

    @PostMapping("/downloadFile")
    public InputStream downloadFile(@RequestParam String filename, HttpServletResponse response) {
        return minioUtil.downloadFile(properties.getBucketName(), filename, response);
    }

    //корректность работы метода не проверял
    @DeleteMapping("/deleteFile")
    public ResponseEntity<Integer> deleteFile(@RequestParam("filename") String filename) {
        minioUtil.deleteFile(properties.getBucketName(), filename);
        return ResponseEntity.ok(HttpStatus.OK.value());
    }
}
