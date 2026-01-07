package io.darbata.basecampapi.auth.internal.request;

import java.util.UUID;

public record AuthUserRequest(
        UUID id,
        String email,
        String name
) {
}
