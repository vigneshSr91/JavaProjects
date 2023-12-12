package com.sap.bulletinboard.ads.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

    /* The time to establish the connection with the target host */
    private static final int CONNECT_TIMEOUT_MILLISECONDS = 3000;

    /*
     * Specifies the maximum duration allowed between each network-level read
     * operation (after connecting)
     */
    private static final int READ_TIMEOUT_SECONDS = 4;

    /*
     * Specifies the maximum duration allowed between each network-level write
     * operation (after connecting)
     */
    private static final int WRITE_TIMEOUT_SECONDS = 4;

    /* The time to wait for a connection from the connection manager/pool */
    private static final int PENDING_ACQUIRE_TIMEOUT_SECONDS = 2;

    /* How often the Connection Pool is checked for evictable connections */
    private static final int EVICTION_TIMEOUT_MINUTES = 15;

    /* Maximum size of the Connection Pool */
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 200;

    @Bean
    public WebClient webClient() {
	WebClient webClient = WebClient.builder().clientConnector(getReactorClientHttpConnector()).build();
	return webClient;
    }

    private ReactorClientHttpConnector getReactorClientHttpConnector() {

	HttpClient httpClient = HttpClient.create(getConnectionProvider())
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLISECONDS)
		.doOnConnected(conn -> conn.addHandler(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
			.addHandler(new WriteTimeoutHandler(WRITE_TIMEOUT_SECONDS)))
		.proxyWithSystemProperties();

	return new ReactorClientHttpConnector(httpClient);
    }

    private ConnectionProvider getConnectionProvider() {

	ConnectionProvider provider = ConnectionProvider.builder("custom").maxConnections(DEFAULT_MAX_TOTAL_CONNECTIONS)
		.pendingAcquireTimeout(Duration.ofSeconds(PENDING_ACQUIRE_TIMEOUT_SECONDS))
		.evictInBackground(Duration.ofMinutes(EVICTION_TIMEOUT_MINUTES)).build();

	return provider;
    }
}