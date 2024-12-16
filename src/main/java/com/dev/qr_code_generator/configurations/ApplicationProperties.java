package com.dev.qr_code_generator.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "properties")
public class ApplicationProperties {

    private QrParameter qrParameter;
    private PdfParameter pdfParameter;

    @Getter
    @Setter
    public static class QrParameter {
        private int width;
        private int height;
    }

    @Getter
    @Setter
    public static class PdfParameter {
        private String absolutePathToFile;
    }
}
