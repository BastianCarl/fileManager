package com.example.demo.files;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.model.FileMetadata;
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
//    public Bucket getBucket() {
//        return s3client.listBuckets().stream().filter(element -> element.getName().equals(BUCKET_NAME)).findFirst().get();
//    }
    public void uploadFile(MultipartFile file) throws IOException {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentType(file.getContentType());
        InputStream in = file.getInputStream();
        PutObjectRequest req = new PutObjectRequest(BUCKET_NAME, generateKey(file), in, meta);
        s3client.putObject(req);
//        return in.readAllBytes();
    }

    @Override
    public void deletedFile(MultipartFile file) {
        s3client.deleteObject(BUCKET_NAME, generateKey(file));
    }

    public void uploadFile(File file) throws IOException {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.length());
        meta.setContentType(Files.probeContentType(file.toPath()));

        InputStream in = new FileInputStream(file);
        PutObjectRequest req =
                new PutObjectRequest(BUCKET_NAME, file.getName(), in, meta);

        s3client.putObject(req);
    }

    private String generatePresignedUrl(String awsKey) {
        return s3client.generatePresignedUrl(BUCKET_NAME, awsKey, new Date(System.currentTimeMillis() + EXPIRATION)).toString();
    }

//    @Cacheable(value = "myCache", key = "#fileMetadata.name")
    public byte[] downloadFile(FileMetadata fileMetadata) throws IOException {
        System.err.println("No download happened");
        String awsUrl = generatePresignedUrl(generateKey(fileMetadata));
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
