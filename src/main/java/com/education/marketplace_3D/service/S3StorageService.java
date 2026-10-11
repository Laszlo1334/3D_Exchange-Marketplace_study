package com.education.marketplace_3D.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {
    private final S3Client s3Client;

    @Value ("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    public String uploadFile (MultipartFile file){
        if (file.isEmpty()){
            throw new IllegalArgumentException("File is empty");
        }
        String name = UUID.randomUUID() + "__"+ file.getOriginalFilename();
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(name).build();
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        return endpoint + "/" + bucketName + "/" + name;
    }

    @PostConstruct
    public void createBucketIfMissing() {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            System.out.println("Bucket successfully created: " + bucketName);
        } catch (Exception e) {
            // Якщо бакет вже існує, AWS SDK викине виняток, ми його просто ігноруємо
        }
    }

//    @PostConstruct
//    public void init() {
//        try {
//            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
//            System.out.println("Bucket '" + bucketName + "' already exists.");
//        } catch (Exception e) {
//            try {
//                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
//                System.out.println("Bucket '" + bucketName + "' created successfully.");
//            } catch (Exception ex) {
//                System.err.println("FAILED TO CONNECT TO LOCALSTACK: " + ex.getMessage());
//            }
//        }
//    }
}
