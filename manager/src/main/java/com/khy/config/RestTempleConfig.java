package com.khy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/***
 * 设置resttemple 的相关参数内容信息
 * @author kanghanyu
 *
 */
@Configuration
@ConfigurationProperties(prefix = "custom.rest.connection")
public class RestTempleConfig {
	
	private Integer connectionRequestTimeout;
	private Integer connectTimeout;
	private Integer readTimeout;

	@Autowired
	private RestTemplateBuilder builder;

	@Bean
	public RestTemplate restTemplate() {
		builder.setConnectTimeout(connectTimeout);
		builder.setReadTimeout(readTimeout);
		return builder.build();
	}
	public Integer getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}
	public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}
	public Integer getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public Integer getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
}
