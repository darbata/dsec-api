package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.GithubApiClient;
import io.darbata.basecampapi.github.internal.InstallationAccessToken;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InstallationAccessTokenService {

    //private final JwtEncoder jwtEncoder;
    private final GithubApiClient githubApiClient;

    private final String clientId = "";

    public InstallationAccessTokenService(GithubApiClient githubApiClient) {
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
        return "";
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