package com.rest.configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.rest.client.ConnectionConfiguration;
import com.rest.constants.Constants;

/**
 * @author nisshukl0
 *
 */
@Configuration
@EnableScheduling
public class RestConnectionConfiguration implements ConnectionConfiguration {
	private static final Log LOGGER = LogFactory.getLog(RestConnectionConfiguration.class);

	/* 
	 * STEPS:
	 * Create socket factory registry and register connection socket factory.
	 * Create pooling connection manager using socket factory registry.
	 */
	@Bean
	public HttpClientConnectionManager httpClientConnectionManager() {
		SSLConnectionSocketFactory sslConnectionSocketFactory = createSSLSocketFactory();
		
		Registry<ConnectionSocketFactory> sslSocketFactoryRegistry = 
				RegistryBuilder.<ConnectionSocketFactory>create()
				.register(Constants.ConnectionConstants.HTTPS, sslConnectionSocketFactory)
				.register(Constants.ConnectionConstants.HTTP, new PlainConnectionSocketFactory())
				.build();
		
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = 
				new PoolingHttpClientConnectionManager(sslSocketFactoryRegistry);
		poolingHttpClientConnectionManager
				.setDefaultMaxPerRoute(Constants.ConnectionConstants.DEFAULT_MAX_PER_ROUTE);
		poolingHttpClientConnectionManager
				.setMaxTotal(Constants.ConnectionConstants.MAX_TOTAL_CONNECTIONS);
		return poolingHttpClientConnectionManager;
	}

	/* 
	 * STEPS:
	 * Implement the ConnectionKeepAliveStrategy interface.
	 * Set the Keep Alive Duration.
	 * To match with header timeouts, loop on header to find
	 * Keep-Alive header if exists, return timeout * 1000
	 */
	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (response, context) -> {
			HeaderElementIterator iterator = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (iterator.hasNext()) {
				HeaderElement headerElement =  iterator.nextElement();
				String key = headerElement.getName();
				String value = headerElement.getValue();
				if (null != value && key.equalsIgnoreCase(Constants.ConnectionConstants.TIMEOUT)) {
					return Long.parseLong(value) * 1000;
				}
			}
			return Constants.ConnectionConstants.DEFAULT_KEEP_ALIVE_DURATION;
			};
	}
	
	/*
	 * STEPS:
	 * Create request configuration.
	 * Set timeouts into request configuration.
	 * Create custom closable client.
	 * Add request configuration into client.
	 * Set connection manager and keep alive strategy.
	 * Build
	 */
	@Bean
	public HttpClient httpClient(HttpClientConnectionManager httpClientConnectionManager,
												ConnectionKeepAliveStrategy connectionKeepAliveStrategy) {
		RequestConfig requestConfiguration = 
					RequestConfig.custom()
								.setConnectTimeout(Constants.ConnectionConstants.CONNECTION_TIMEOUT)
								.setConnectionRequestTimeout(Constants.ConnectionConstants.REQUEST_TIMEOUT)
								.setSocketTimeout(Constants.ConnectionConstants.SOCKET_TIMEOUT)
								.build();
		
		return HttpClients.custom()
								.setDefaultRequestConfig(requestConfiguration)
								.setConnectionManager(httpClientConnectionManager)
								.setKeepAliveStrategy(connectionKeepAliveStrategy)
								.build();
	}

	/* 
	 * STEPS:
	 * Create a runnable implementation.
	 * Provide scheduled duration for monitoring
	 * Close expired connections
	 * Close Idle Connections
	 */
	@Bean
	public Runnable getIdleConnectionMonitor(HttpClientConnectionManager httpClientConnectionManager) {
		return new Runnable() {
			@Scheduled(fixedDelay = Constants.ConnectionConstants.IDEL_CONNECTION_MONITOR_PERIOD)
			public void run() {
				if (null != httpClientConnectionManager) {
					LOGGER.info("Idel Connection Monitor: closing idle connections");
					httpClientConnectionManager.closeExpiredConnections();
					httpClientConnectionManager
						.closeIdleConnections(Constants.ConnectionConstants.CLOSE_IDLE_CONNECTION_WAIT_TIME, TimeUnit.MILLISECONDS);
				}
			}
		};
	}
	
	/**
	 * This method creates an SSL Socket Factory for Registering
	 * to Connection Manager.
	 * STEPS: 
	 * Create SSL context builder.
	 * Load trust material into context builder.
	 * Create SSL connection socket factory via SSL context builder.
	 */
	private SSLConnectionSocketFactory createSSLSocketFactory() {
		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		SSLConnectionSocketFactory sslConnectionSocketFactory = null;
		try {
			sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.error("Error occured while loading SSL Trust Material : " + e);
		}
		
		try {
			sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			LOGGER.error("Error occured while creating socket factory : " + e);
		}
		return sslConnectionSocketFactory;
	}
}
