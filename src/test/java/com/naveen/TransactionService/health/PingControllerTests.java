package com.naveen.TransactionService.health;

import com.naveen.TransactionService.dto.Ping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PingControllerTests {

    @Autowired
    WebTestClient webTestClient;

    public URI healthCheckUrl(UriBuilder uriBuilder) {
        return uriBuilder
                .path("/ping")
                .build();
    }

    @Test
    @DisplayName("Health check API endpoint test, success")
    void testHealthCheckEndPoint() {

        webTestClient.get()
                .uri(this::healthCheckUrl)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("serverTime").isNotEmpty()
                .jsonPath("serverTime").value(serverTimeString -> {
                    Instant serverTime = Instant.parse(serverTimeString.toString());
                    assertThat(serverTime).isStrictlyBetween(Instant.now().minusMillis(10000), Instant.now());
                });

    }

}
