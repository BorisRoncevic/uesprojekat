package com.app.godo.services.files;

import com.app.godo.services.venue.VenueService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinIOService {
    private final MinioClient minioClient;

    private static final Logger logger = LogManager.getLogger(MinIOService.class);

    @Value("${spring.minio.bucket}")
    private String bucketName;

    @PostConstruct
    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("MinIO bucket '{}' created successfully on startup.", bucketName);
            } else {
                logger.info("MinIO bucket '{}' already exists.", bucketName);
            }

            String publicReadPolicy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(publicReadPolicy)
                            .build()
            );
            logger.info("Public read-only policy successfully applied to MinIO bucket '{}'.", bucketName);

        } catch (Exception e) {
            logger.error("Failed to initialize MinIO bucket on startup: ", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String uniqueFilename = UUID.randomUUID() + "_" + originalFilename.replaceAll("\\s+", "_");

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uniqueFilename)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            }

            logger.info("Successfully uploaded file to MinIO: {}", uniqueFilename);
            return uniqueFilename;

        } catch (Exception e) {
            logger.error("MinIO upload failed: ", e);
            throw new RuntimeException("Could not upload file to storage server.", e);
        }
    }

    public String getFileUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        // Since the bucket policy is public-read, we do not need S3 pre-signed signatures!
        // We just return a direct, permanent public localhost link:
        return "http://localhost:9000/" + bucketName + "/" + filename;
    }

    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            logger.warn("Attempted to delete a file, but the provided filename was null or empty.");
            return;
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
            logger.info("Successfully deleted file from MinIO: {}", filename);
        } catch (Exception e) {
            logger.error("Failed to delete file from MinIO: {}", filename, e);
            throw new RuntimeException("Could not delete file from storage server.", e);
        }
    }
}
