package com.dev.qr_code_generator.services;

import com.dev.qr_code_generator.configurations.ApplicationProperties;
import com.dev.qr_code_generator.configurations.minio.StorageProperties;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QRService {

    ApplicationProperties properties;
    StorageProperties storageProperties;
    MinioService minioService;

    /**
     * Создать QR код на основании переданного файла
     */
    public BitMatrix createFromFile(MultipartFile multipartFile) {
        return create(multipartFile, null);
    }

    /**
     * Создать QR код на основании необходимого URL
     */
    public BitMatrix createWithUrl(String url) {
        return create(null, url);
    }

    /**
     * Получить значение QR кода
     */
    public String scanQRCode(File qrCodeFile) {
        return scan(qrCodeFile);
    }

    private String scan(File qrCodeFile) {
        try {
            log.info("Scanning qr code started");
            BufferedImage bufferedImage = ImageIO.read(qrCodeFile);
            BufferedImageLuminanceSource bufferedImageLuminanceSource = new BufferedImageLuminanceSource(bufferedImage);
            HybridBinarizer hybridBinarizer = new HybridBinarizer(bufferedImageLuminanceSource);
            BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);
            MultiFormatReader multiFormatReader = new MultiFormatReader();

            Result result = multiFormatReader.decode(binaryBitmap);
            return result.getText();
        } catch (IOException | NotFoundException e) {
            log.error("Error during reading QR code image: {}", e.getMessage());
            return "Error during reading QR code image";
        }
    }

    private BitMatrix create(MultipartFile file, String url) {
        try {
            BitMatrix result;
            log.info("Generating qr code");
            if (file != null) {
                String bucketName = storageProperties.getBucketName();
                minioService.uploadFile(file, bucketName);
                String previewFileUrl = minioService.getPreviewFileUrl(bucketName, file.getOriginalFilename());
                result = encode(previewFileUrl);
            } else {
                result = encode(url);
            }
            return result;
        } catch (WriterException e) {
            log.warn("Could not encode qr code");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private BitMatrix encode(String previewFileUrl) throws WriterException {
        QRCodeWriter qrWriter = new QRCodeWriter();
        ApplicationProperties.QrParameter qrParameter = properties.getQrParameter();
        return qrWriter.encode(
                previewFileUrl,
                BarcodeFormat.QR_CODE,
                qrParameter.getWidth(),
                qrParameter.getHeight()
        );
    }
}