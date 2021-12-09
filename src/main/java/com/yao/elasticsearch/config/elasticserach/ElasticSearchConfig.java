package com.yao.elasticsearch.config.elasticserach;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

/**
 * Created by yaojian on 2021/11/8 16:33
 *
 * @author
 */

@Configuration
public class ElasticSearchConfig {

    @Bean
    RestHighLevelClient client() {

        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("139.224.162.184:9200")
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}


