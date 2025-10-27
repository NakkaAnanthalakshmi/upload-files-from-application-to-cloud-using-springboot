package com.example.certificate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class AwsS3Service {

    private final String bucketName;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public AwsS3Service(
            S3Client s3Client,
            @Value("${aws.access.key.id}") String accessKeyId,
            @Value("${aws.secret.access.key}") String secretAccessKey,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket-name}") String bucketName
    ) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;

        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                        )
                )
                .build();
    }

    // Upload file to S3
    public String uploadFile(String key, byte[] fileData, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObject(objectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileData));

        return key;
    }

    // Generate Pre-Signed URL valid for limited time for downloading
    public String generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // URL valid for 15 minutes
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
