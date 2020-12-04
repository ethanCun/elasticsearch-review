package com.ethan.es1.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//注入es需要的HttoHighLevelClient， 然后就可以使用这个client
@Configuration
public class ESConfig {

    //注入es需要的RestHighLevelClient bean
    @Bean("client")
    public RestHighLevelClient elasticsearchClient(){

        //集群可以配置多个客户端HttpHost
        RestHighLevelClient restHighLevelClient =
                new RestHighLevelClient(
                        RestClient.builder(
                                new HttpHost("localhost",
                                        9200,
                                        "http")
                        )
                );

        return restHighLevelClient;
    }
}
