package com.dev.qr_code_generator.controllers;

import com.dev.qr_code_generator.services.QRService;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeneratorController {

    QRService qrService;

    @GetMapping("/createFromFile")
    public void create(@RequestParam("file") MultipartFile multipartFile, HttpServletResponse response) {
        BitMatrix result = qrService.createFromFile(multipartFile);
        fillResponse(response, result);
    }

    @GetMapping("/createWithUrl")
    public void createWithUrl(@RequestParam("url") String url, HttpServletResponse response) {
        BitMatrix result = qrService.createWithUrl(url);
        fillResponse(response, result);
    }

    @PostMapping("/scan")
    public ResponseEntity<String> scan(@RequestParam("qrCodePNG") MultipartFile qrCodePNG) {
        File tempFile;
        try {
            tempFile = File.createTempFile("qrCode", ".png");
            qrCodePNG.transferTo(tempFile);
            String qrCodeText = qrService.scanQRCode(tempFile);
            tempFile.deleteOnExit();
            log.info("The read-method worked correctly");
            return ResponseEntity.ok(qrCodeText);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }

    private static void fillResponse(HttpServletResponse response, BitMatrix result) {
        try {
            response.setContentType("image/png");
            MatrixToImageWriter.writeToStream(result, "PNG", response.getOutputStream());
            log.info("The create-method worked correctly");
        } catch (IOException e) {
            log.error("An IO error occurred during method execution");
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
