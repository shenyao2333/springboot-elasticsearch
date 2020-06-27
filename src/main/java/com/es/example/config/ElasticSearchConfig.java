package com.es.example.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sy
 * @date Created in 2020.6.26 23:05
 * @description
 */
@Configuration
public class ElasticSearchConfig {


    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        //new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9200, "http")));

        return client;
    }


}
