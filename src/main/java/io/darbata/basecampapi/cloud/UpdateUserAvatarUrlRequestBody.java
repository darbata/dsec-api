package io.darbata.basecampapi.cloud;

public record UpdateUserAvatarUrlRequestBody(
        String userPoolId,
        String userName, // sub
        String newAvatarUrl
) {}