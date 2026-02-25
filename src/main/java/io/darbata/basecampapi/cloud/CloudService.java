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

    @Value("${aws.cognito.user-pool.id}")
    private String cognitoPoolId;

    private final ApiGatewayClient  apiGatewayClient;

    public CloudService(ApiGatewayClient apiGatewayClient) {
        this.apiGatewayClient = apiGatewayClient;
    }

    public void updateUserAvatarUrl(String userId, String avatarUrl) {
        UpdateUserAvatarUrlRequestBody body = new UpdateUserAvatarUrlRequestBody(
                cognitoPoolId,
                userId,
                avatarUrl
        );
        apiGatewayClient.updateUserAvatarUrl(body);
    }

    public String createUserAvaterPutUrl(String path, String contentType) {
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

    public void updateUserDisplayName(String id, String displayName) {
        UpdateUserUsernameRequestBody body = new UpdateUserUsernameRequestBody(
                cognitoPoolId,
                id,
                displayName
        );
        apiGatewayClient.updateUserUsername(body);
    }
}