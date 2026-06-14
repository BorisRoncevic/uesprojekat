package com.app.godo.utils;

import com.app.godo.services.venue.VenueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public class PDFParserUtil {
    private static final Logger logger = LogManager.getLogger(PDFParserUtil.class);

    public static String extractTextFromPDF(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Attempted to parse an empty or null file.");
            return "";
        }

        // Quick check to ensure the client actually sent a PDF
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            logger.warn("Uploaded file is not a PDF. Content-Type: {}", file.getContentType());
            return "";
        }

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();
            String rawText = pdfStripper.getText(document);

            if (rawText != null) {
                return rawText.replaceAll("\\r\\n|\\r|\\n|\\t", " ")
                        .replaceAll("\\s+", " ")
                        .trim();
            }

            return "";
        } catch (Exception e) {
            logger.error("Failed to parse PDF document: ", e);
            return "";
        }
    }
}
