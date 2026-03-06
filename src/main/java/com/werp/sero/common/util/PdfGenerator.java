package com.werp.sero.common.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.werp.sero.common.error.ErrorCode;
import com.werp.sero.common.error.exception.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class PdfGenerator {
    public static byte[] generate(final String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            log.error("PDF 생성 실패", e);
            throw new SystemException(ErrorCode.PDF_GENERATION_FAILED);
        }
    }
}