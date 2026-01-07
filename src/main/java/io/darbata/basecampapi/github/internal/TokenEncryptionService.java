package io.darbata.basecampapi.github.internal;

import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class TokenEncryptionService {
    private final TextEncryptor encryptor;

    public TokenEncryptionService(
            @Value("${encryption.token.password}") String tokenPassword,
            @Value("${encryption.token.salt}") String tokenSalt
    ) {
        this.encryptor = Encryptors.text(tokenPassword, tokenSalt);
    }

    public GithubToken encrypt(GithubToken token) {
        return new GithubToken(
                token.userId(),
                this.encryptor.encrypt(token.accessToken()),
                token.accessTokenExpiryDate(),
                this.encryptor.encrypt(token.refreshToken()),
                token.refreshTokenExpiryDate(),
                token.scope(),
                token.tokenType()
        );
    }

    public GithubToken decrypt(GithubToken token) {
        return new GithubToken(
                token.userId(),
                this.encryptor.decrypt(token.accessToken()),
                token.accessTokenExpiryDate(),
                this.encryptor.decrypt(token.refreshToken()),
                token.refreshTokenExpiryDate(),
                token.scope(),
                token.tokenType()
        );
    }
}
