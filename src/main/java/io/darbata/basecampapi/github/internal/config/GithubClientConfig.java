package io.darbata.basecampapi.github.internal.config;

import io.darbata.basecampapi.github.internal.GithubApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class GithubClientConfig {


    public GithubClientConfig() {}

    @Bean
    public GithubApiClient githubApiClient() {

        var factory = new BufferingClientHttpRequestFactory(new JdkClientHttpRequestFactory());

        RestClient restClient = RestClient
                .builder()
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                .build();

        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

        return proxyFactory.createClient(GithubApiClient.class);
    }



}
