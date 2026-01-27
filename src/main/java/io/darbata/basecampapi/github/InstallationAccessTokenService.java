package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.GithubApiClient;
import io.darbata.basecampapi.github.internal.InstallationAccessToken;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

@Service
public class InstallationAccessTokenService {

    private final JwtEncoder jwtEncoder;
    private final GithubApiClient githubApiClient;

    private final String clientId = "";

    public InstallationAccessTokenService(JwtEncoder jwtEncoder, GithubApiClient githubApiClient) {
        this.jwtEncoder = jwtEncoder;
        this.githubApiClient = githubApiClient;
    }

    public String generateJwtToken() {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(clientId)
                .issuedAt(now.minusSeconds(60))
                .expiresAt(now.plusSeconds(540)) // 9 minutes
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public InstallationAccessToken getInstallationAccessToken() {
        int installationId = 2431127;
        String jwt = generateJwtToken();
        return githubApiClient.getInstallationAccessToken(
                "Bearer " + jwt,
                installationId
        );
    }



}