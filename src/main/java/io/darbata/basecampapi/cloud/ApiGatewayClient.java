package io.darbata.basecampapi.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange
@Service
public interface ApiGatewayClient {
    @Value("${aws.api-gateway.invoke-url}")
    String invokeUrl = "https://18zs1pqrcc.execute-api.ap-southeast-2.amazonaws.com/";

    @PutExchange(value = invokeUrl + "/user/avatar")
    void updateUserAvatarUrl(
            @RequestBody UpdateUserAvatarUrlRequestBody body
    );

    @PutExchange(value = invokeUrl + "/user/username")
    void updateUserUsername(
            @RequestBody UpdateUserUsernameRequestBody body
    );
}
