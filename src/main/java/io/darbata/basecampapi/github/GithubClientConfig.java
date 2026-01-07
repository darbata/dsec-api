package io.darbata.basecampapi.github;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class GithubClientConfig {

    private final Logger logger;

    public GithubClientConfig(Logger logger) {
        this.logger = logger;
    }

    @Bean
    public GithubApiClient githubApiClient() {

        var factory = new BufferingClientHttpRequestFactory(new JdkClientHttpRequestFactory());

        RestClient restClient = RestClient
                .builder()
                .requestFactory(factory)
                .requestInterceptor((request, bytes, execution) -> {
                    logger.logRequest(request, bytes);
                    var response = execution.execute(request, bytes);
                    return logger.logResponse(response);
                })
                .defaultHeader("Accept", "application/json")
                .build();

        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

        return proxyFactory.createClient(GithubApiClient.class);
    }



}
