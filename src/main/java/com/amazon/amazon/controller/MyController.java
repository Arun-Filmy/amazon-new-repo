package com.amazon.amazon.controller;

import com.amazon.amazon.service.AmazonS3Service;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class MyController {

    private final AmazonS3Service amazonS3Service;

    public MyController(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String folderName = getFileExtension(file.getOriginalFilename());
        amazonS3Service.createFolder(folderName);
        InputStream inputStream = file.getInputStream();
        amazonS3Service.uploadFile(folderName, file.getOriginalFilename(), file.getContentType(), inputStream);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    @GetMapping("/file/{folderName}/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String folderName, @PathVariable String fileName) {
        S3Object s3Object = amazonS3Service.getFile(folderName, fileName);
        InputStream inputStream = s3Object.getObjectContent();
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()));
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/file/{folderName}/{fileName}")
    public void writeFile(@PathVariable String folderName, @PathVariable String fileName,
                          @RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        amazonS3Service.writeFile(folderName, fileName, inputStream);
    }
}
