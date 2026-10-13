package com.education.marketplace_3D.controller;

import com.education.marketplace_3D.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    private final S3StorageService s3StorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile (@RequestParam("file")MultipartFile file){
        String url = s3StorageService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @PostMapping(value = "/upload/async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFileAsync (@RequestParam("file")MultipartFile file){
        File tempFile;
        try {
            tempFile = File.createTempFile("upload_", ".tmp");
            file.transferTo(tempFile);
        } catch (IOException e){
            throw new RuntimeException("Could not create temp file", e);
        }
        s3StorageService.uploadFileAsync(tempFile, file.getOriginalFilename());
        return ResponseEntity.accepted().body("File accepted for work!");
    }
}
