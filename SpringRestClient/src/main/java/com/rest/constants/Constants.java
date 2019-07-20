package com.rest.constants;

public class Constants {

	private Constants() {
		// do nothing
	}

	public static class ConnectionConstants {

		private ConnectionConstants() {
			// do nothing
		}

		public static final int CONNECTION_TIMEOUT = 10000;
		public static final int REQUEST_TIMEOUT = 10000;
		public static final int SOCKET_TIMEOUT = 10000;
		public static final String HTTPS = "https";
		public static final String HTTP = "http";
		public static final int DEFAULT_MAX_PER_ROUTE = 5;
		public static final int MAX_TOTAL_CONNECTIONS = 10;
		public static final long DEFAULT_KEEP_ALIVE_DURATION = 30000;
		public static final String TIMEOUT = "timeout";
		public static final long IDEL_CONNECTION_MONITOR_PERIOD = 10000;
		public static final long CLOSE_IDLE_CONNECTION_WAIT_TIME = 10000;

	}

}
