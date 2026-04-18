package com.mosquizto.api.configuration;


import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@Slf4j
public class MeiliSearchConfig {
    @Value("${meilisearch.host}")
    String host ;
    @Value("${meilisearch.masterKey}")
    String masterKey ;

    @Bean(name = "meiliSearch")
    public Client meiliClient()
    {
        return new Client(new Config(host, masterKey));
    }
}
