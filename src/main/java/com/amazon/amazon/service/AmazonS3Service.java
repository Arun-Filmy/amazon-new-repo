package com.amazon.amazon.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class AmazonS3Service {

    private final AmazonS3 amazonS3;

    public AmazonS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void createFolder(String folderName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        PutObjectRequest putObjectRequest = new PutObjectRequest("javafilesstoragebucket", folderName + "/", emptyContent, metadata);
        amazonS3.putObject(putObjectRequest);
    }

    public void uploadFile(String folderName, String fileName, String fileType, InputStream inputStream) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(fileType);
        PutObjectRequest putObjectRequest = new PutObjectRequest("javafilesstoragebucket", folderName + "/" + fileName, inputStream, metadata);
        amazonS3.putObject(putObjectRequest);
    }

    public S3Object getFile(String folderName, String fileName) {
        GetObjectRequest getObjectRequest = new GetObjectRequest("javafilesstoragebucket", folderName + "/" + fileName);
        return amazonS3.getObject(getObjectRequest);
    }

    public void writeFile(String folderName, String fileName, InputStream inputStream) {
        ObjectMetadata metadata = new ObjectMetadata();
        try {
            metadata.setContentLength(inputStream.available());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        metadata.setContentType("text/plain");
        PutObjectRequest putObjectRequest = new PutObjectRequest("javafilesstoragebucket", folderName + "/" + fileName, inputStream, metadata);
        amazonS3.putObject(putObjectRequest);
    }

}