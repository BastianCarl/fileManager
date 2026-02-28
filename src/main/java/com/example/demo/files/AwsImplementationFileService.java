package com.example.demo.files;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.exception.FileServiceFailure;
import com.example.demo.model.FileMetadata;
import com.example.demo.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;
@Service
public class AwsImplementationFileService implements FileService {

    @Value("${aws.bucket.name}")
    private String BUCKET_NAME;
    @Value("${aws.presigned.expiration}")
    private int EXPIRATION;
    private static final Regions REGION = Regions.EU_CENTRAL_1;
    private static final String ACCESS_KEY = "AWS_ACCESS_KEY_ID";
    private static final String SECRET_KEY = "AWS_SECRET_ACCESS_KEY";
    private static Logger LOGGER = LoggerFactory.getLogger(AwsImplementationFileService.class);
    private static AmazonS3 s3client;
    static {
        s3client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(System.getenv(ACCESS_KEY), System.getenv(SECRET_KEY))
                        )
                )
                .build();
    }

    public void uploadFile(Resource resource) {
        switch (resource.getSource()) {
            case File file -> this.uploadFile(file, resource.getFileMetadata());
            case MultipartFile multipartFile -> this.uploadMultipartFile(multipartFile, resource.getFileMetadata());
            default -> throw new RuntimeException("Unsupported file type: " + resource.getSource());
        }

    }
    private void uploadMultipartFile(MultipartFile file, FileMetadata fileMetadata) {
        ObjectMetadata meta = new ObjectMetadata();
        InputStream in = null;
        meta.setContentLength(file.getSize());
        meta.setContentType(file.getContentType());
        try {
            in = file.getInputStream();
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage());
        }
        callS3Client(fileMetadata.getKey(), in, meta);
    }

    private void uploadFile(File file, FileMetadata fileMetadata) {
        ObjectMetadata meta = new ObjectMetadata();
        InputStream in = null;
        meta.setContentLength(file.length());
        try {
            meta.setContentType(Files.probeContentType(file.toPath()));
            in = new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        callS3Client(fileMetadata.getKey(), in, meta);
    }

    private void callS3Client(String key, InputStream in, ObjectMetadata meta) {
        PutObjectRequest req = new PutObjectRequest(BUCKET_NAME, key, in, meta);
        try {
            s3client.putObject(req);
        } catch (AmazonClientException exception) {
            throw new FileServiceFailure("Failed to upload file to AWS", exception);
        }
    }

    private String generatePresignedUrl(String awsKey) {
        return s3client.generatePresignedUrl(BUCKET_NAME, awsKey, new Date(System.currentTimeMillis() + EXPIRATION)).toString();
    }

//    @Cacheable(value = "myCache", key = "#fileMetadata.name")
    public byte[] downloadFile(FileMetadata fileMetadata) throws IOException {
        String awsUrl = generatePresignedUrl(fileMetadata.getKey());
        URL url = new URL(awsUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // move to constant
        // use Spring to make GET
        con.setRequestMethod("GET");
        try (InputStream in = con.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            // move to constant
            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        } finally {
            con.disconnect();
        }
    }
}
