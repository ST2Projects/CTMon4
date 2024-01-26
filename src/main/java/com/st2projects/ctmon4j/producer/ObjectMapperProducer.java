package com.st2projects.ctmon4j.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.Produces;

public class ObjectMapperProducer
{
    @Produces
    public ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }
}
