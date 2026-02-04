package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.GithubApiClient;
import io.darbata.basecampapi.github.internal.InstallationAccessToken;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;



@Service
public class InstallationAccessTokenService {

    @Value("classpath:static/dsec-basecamp-pkcs8.pem")
    private Resource privateKeyPem;

    @Value("${github.app.id}")
    private int appId;

    @Value("${github.app.installation.id}")
    private int installationId;



    //private final JwtEncoder jwtEncoder;
    private final GithubApiClient githubApiClient;

    public InstallationAccessTokenService(GithubApiClient githubApiClient) {
        this.githubApiClient = githubApiClient;
    }

    private PrivateKey getPrivateKey() throws Exception {
        try (PemReader pemReader = new PemReader(new InputStreamReader(privateKeyPem.getInputStream(), StandardCharsets.UTF_8))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        }
    }

    private String generateJwtToken() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        long now = System.currentTimeMillis();
        Date currentDate = new Date(now - 60000);
        Date expiryDate = new Date(now + 600000); // 10 minutes

        return Jwts.builder()
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .issuer(Integer.toString(appId))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public InstallationAccessToken getInstallationAccessToken() throws Exception {
        String jwt = generateJwtToken();
        return githubApiClient.getInstallationAccessToken(
                "Bearer " + jwt,
                installationId
        );
    }



}