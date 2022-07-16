package com.atguigu.gulimall.search.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-23 12:32
 **/
@Configuration
public class GulimallElasticSearchConfig {
    public static final RequestOptions COMMON_OPTIONS;
    public static final String username = "elastic";
    public static final String password = "zzc123456";
    public static final String host = "116.205.234.211";
    public static final int port = 9200;
    public static final String scheme = "http";

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // builder.addHeader("Authorization", "Bearer " + TOKEN);
        // builder.setHttpAsyncResponseConsumerFactory(
        //         new HttpAsyncResponseConsumerFactory
        //                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean(destroyMethod ="close")
    public RestHighLevelClient esRestClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        Credentials creds = new UsernamePasswordCredentials(username, password);
        credentialsProvider.setCredentials(AuthScope.ANY, creds);
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port, scheme))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        return new RestHighLevelClient(restClientBuilder);
//        RestHighLevelClient client =
//                new RestHighLevelClient(RestClient.builder(new HttpHost("116.205.234.211", 9200, "http")));
//        return client;

    }

}
