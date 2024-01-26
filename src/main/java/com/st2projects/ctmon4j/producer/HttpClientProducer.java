package com.st2projects.ctmon4j.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.net.http.HttpClient;
import java.time.Duration;

public class HttpClientProducer
{
    @Produces
    public HttpClient httpClient()
    {
        return HttpClient.newBuilder()
                .version( HttpClient.Version.HTTP_2 )
                .followRedirects( HttpClient.Redirect.NORMAL )
                .connectTimeout( Duration.ofSeconds(10) )
                .build();
    }
}
