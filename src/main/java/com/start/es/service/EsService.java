package com.start.es.service;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

@Service
public class EsService {
	
	RestHighLevelClient restHighLevelClient;
	
	public EsService() {
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost("127.0.0.1", 9200))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(5000) // 连接超时（默认为1秒）
								.setSocketTimeout(60000);// 套接字超时（默认为30秒）
					}
				})
				// .setMaxRetryTimeoutMillis(60000)//调整最大重试超时时间（默认为30秒）
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder
								.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build());// 线程数
					}
				});
		RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
		
		
		this.restHighLevelClient = client;
	}

	
	public RestHighLevelClient getClient(){
		return restHighLevelClient;
		
	}
	

}
