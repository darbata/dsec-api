
import io.darbata.basecampapi.github.internal.GithubApiClient;
import io.darbata.basecampapi.github.internal.InstallationAccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;


@Service
public class InstallationAccessTokenService {

    @Value("classpath:static/darbata-dsec-collab.2026-01-26.private-key.pem")
    private Resource privateKeyPem;

    //private final JwtEncoder jwtEncoder;
    private final GithubApiClient githubApiClient;

    private final String clientId = "";

    public InstallationAccessTokenService(GithubApiClient githubApiClient) {
        this.githubApiClient = githubApiClient;
    }

    private PrivateKey getPrivateKey() throws Exception {
        String key = new String(privateKeyPem.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        String privateKeyPEM = key
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .trim();

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private String generateJwtToken() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        long now = System.currentTimeMillis();
        Date currentDate = new Date(now - 60000);
        Date expiryDate = new Date(now + 600000); // 10 minutes

        return Jwts.builder()
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer
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