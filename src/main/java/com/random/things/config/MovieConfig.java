package com.random.things.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MovieConfig {

    @Value("${movie.ApiKey}")
    String key;

    @Value("${movie.Url}")
    String url;


}
