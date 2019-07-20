package com.rest.configuration;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.rest.client.RestTemplateConfiguration;

@Configuration
public class RestTemplateConfigImpl implements RestTemplateConfiguration {

	@Autowired
	HttpClient httpClient;

	/*
	 * ClientHttpRequestFactory consumes a configured client to setup pooling for
	 * Spring's Rest Template.
	 */
	@Override
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient);
		return clientHttpRequestFactory();
	}

	/*
	 * Spring MVC's RestTemplate doesn't use HTTP connection pooling of any kind,
	 * and will establish and close a connection every time you make a REST call. To
	 * use connection pooling, we need to provide another implementation of
	 * ClientHttpRequestFactory.
	 */
	@Override
	public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
		return new RestTemplate(clientHttpRequestFactory);
	}

}
