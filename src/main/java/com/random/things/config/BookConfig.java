package com.random.things.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class BookConfig {

    @Value("${book.ApiKey}")
    String key;

    @Value("${book.Url}")
    String url;
}
