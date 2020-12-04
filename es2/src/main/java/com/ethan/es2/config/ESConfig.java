package com.ethan.es2.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ESConfig {

    //注入elasticsearch的高级客户端
    @Bean
    public RestHighLevelClient restHighLevelClient(){

        RestClientBuilder restClientBuilder =
                RestClient.builder(new HttpHost("localhost",
                        9200,
                        "http"));

        RestHighLevelClient restHighLevelClient =
                new RestHighLevelClient(restClientBuilder);

        return restHighLevelClient;
    }
}
