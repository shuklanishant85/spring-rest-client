package com.rest.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;

/**
 * @author nisshukl0
 *
 *         While creating a Rest Client using spring, one requires a client
 *         implementation, an HTTP connection manager to manage the client, a
 *         connection keep alive strategy to control connection pooling, and an
 *         idle connections monitor.
 *
 */
public interface ConnectionConfiguration {

	/**
	 * HTTP Client is HTTP transport library interface whose purpose is to transmit
	 * and receive HTTP messages. The following functionalities can be implemented
	 * for HTTP Client : connection management, state management, authentication and
	 * redirection handling.
	 */
	HttpClient httpClient(HttpClientConnectionManager httpClientConnectionManager,
			ConnectionKeepAliveStrategy connectionKeepAliveStrategy);

	/**
	 * In a client connection manager, the connections are pooled on a per route
	 * basis. The manager keeps persistent connections available in the pool. A
	 * request for a route is served by leasing a connection from the pool rather
	 * than creating a brand new connection.
	 */
	HttpClientConnectionManager httpClientConnectionManager();

	/**
	 * ConnectionKeepAliveStrategy helps in setting time which decides how long a
	 * connection can remain idle before being reused.
	 */
	ConnectionKeepAliveStrategy connectionKeepAliveStrategy();

	/**
	 * An idleConnectionMonitor thread periodically checks all connections and frees
	 * up those which have not been used for a given idle time.
	 */
	Runnable getIdleConnectionMonitor(HttpClientConnectionManager httpClientConnectionManager);
}
