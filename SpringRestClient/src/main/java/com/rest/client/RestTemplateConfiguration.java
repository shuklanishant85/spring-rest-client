package com.rest.client;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author nisshukl0
 *
 */
public interface RestTemplateConfiguration {

	/**
	 * RestTemplate is used to invoke REST APIs.
	 */
	RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory);

	/**
	 * It uses Apache HttpComponents' HttpClient to create requests. It uses an
	 * HttpClient instance and sets up authentication, HTTP connection pooling, etc.
	 */
	ClientHttpRequestFactory clientHttpRequestFactory();

}
