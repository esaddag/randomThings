package com.random.things.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MusicConfig {

    @Value("${music.ApiKey}")
    String key;

    @Value("${music.Url}")
    String url;

    @Value("${music.TokenUrl}")
    String tokenUrl;
}
