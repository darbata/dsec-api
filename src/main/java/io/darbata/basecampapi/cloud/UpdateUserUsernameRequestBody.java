package io.darbata.basecampapi.cloud;

public record UpdateUserUsernameRequestBody(
        String userPoolId,
        String userName, // sub
        String displayName
) { }
