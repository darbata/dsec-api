package io.darbata.basecampapi.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
public class CloudService {

    @Value("${aws.asset-bucket}")
    private String bucket;

    public String createUserAvaterPutUrl(String userId, String extension, String contentType) {
        String path = "user/" + userId + "/pfp" + extension;
        System.out.println(bucket);
        return createPresignedUrl(bucket, path, contentType);
    }

    private String createPresignedUrl(String bucketName, String path, String contentType) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_SOUTHEAST_2)
                .credentialsProvider(ProfileCredentialsProvider.create("local"))
                .build()
        ) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType(contentType)
                    .key(path)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}