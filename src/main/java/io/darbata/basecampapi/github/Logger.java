package io.darbata.basecampapi.github;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class Logger {
    public void logRequest(HttpRequest request, byte[] body) {
        System.out.println(request.getMethod() + "    " + request.getURI()); // print request
        if (body.length > 0) { // print body
            System.out.println(new String(body, StandardCharsets.UTF_8));
        }
    }

    public ClientHttpResponse logResponse(ClientHttpResponse response) throws IOException {
        byte[] bodyContents = StreamUtils.copyToByteArray(response.getBody());
        System.out.println("Response: " + response.getStatusCode());
        System.out.println(new String(bodyContents, StandardCharsets.UTF_8));
        return response;
    }
}
