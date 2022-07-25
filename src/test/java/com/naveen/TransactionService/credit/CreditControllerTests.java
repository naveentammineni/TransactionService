package com.naveen.TransactionService.credit;


import com.naveen.TransactionService.dto.*;
import com.naveen.TransactionService.model.CurrentTransaction;
import com.naveen.TransactionService.model.CurrentTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreditControllerTests {

    @Autowired
    WebTestClient webTestClient;

    private final String USER_ID = "1";
    @Autowired
    CurrentTransactionRepository currentTransactionRepository;

    public URI creditUrl(UriBuilder uriBuilder, String messageId) {
        return uriBuilder
                .path("/load/"+messageId)
                .build();
    }

    @BeforeAll
    void setup(){
        CurrentTransaction currentTransaction = new CurrentTransaction();
        currentTransaction.setAmount(100);
        currentTransaction.setCurrency("USD");
        currentTransaction.setMessageId(UUID.randomUUID().toString());
        currentTransaction.setUserId(USER_ID);
        currentTransaction.setCreatedAt(Instant.now());
        currentTransaction.setDebitOrCreditType(DebitOrCredit.SNAPSHOT);
        currentTransactionRepository.save(currentTransaction);
    }

    @Test
    @DisplayName("Test the credit transaction, success")
    void testCreditTransaction() {
        LoadRequest loadRequest = new LoadRequest();
        loadRequest.setTransactionAmount(
                Amount.of("100", "USD", DebitOrCredit.CREDIT)
        );
        loadRequest.setUserId(USER_ID);
        String messageId = UUID.randomUUID().toString();
        loadRequest.setMessageId(messageId);
        webTestClient
                .put()
                .uri(uriBuilder -> creditUrl(uriBuilder, messageId))
                .bodyValue(loadRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(LoadResponse.class)
                .value(response -> {
                    assertThat(response.getBalance().getAmount()).isEqualTo("200.0");
                    assertThat(response.getBalance().getCurrency()).isEqualTo("USD");
                    assertThat(response.getMessageId()).isEqualTo(messageId);
                    assertThat(response.getUserId()).isEqualTo(USER_ID);
                });
    }

    @Test
    @DisplayName("Test the credit transaction with empty userId, error")
    void testCreditTransactionWithEmptyUserId() {
        LoadRequest loadRequest = new LoadRequest();
        loadRequest.setTransactionAmount(
                Amount.of("100", "USD", DebitOrCredit.CREDIT)
        );
        String messageId = UUID.randomUUID().toString();
        loadRequest.setMessageId(messageId);
        webTestClient
                .put()
                .uri(uriBuilder -> creditUrl(uriBuilder, messageId))
                .bodyValue(loadRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message").isNotEmpty()
                .jsonPath("message").isEqualTo("userId must not be null");
    }

    @Test
    @DisplayName("Test the credit transaction with empty messageId, error")
    void testCreditTransactionWithEmptyMessageId() {
        LoadRequest loadRequest = new LoadRequest();
        loadRequest.setTransactionAmount(
                Amount.of("100", "USD", DebitOrCredit.CREDIT)
        );
        String messageId = UUID.randomUUID().toString();
        loadRequest.setUserId(USER_ID);
        webTestClient
                .put()
                .uri(uriBuilder -> creditUrl(uriBuilder, messageId))
                .bodyValue(loadRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message").isNotEmpty()
                .jsonPath("message").isEqualTo("messageId must not be null");
    }

    @Test
    @DisplayName("Test the credit transaction with null Amount, error")
    void testCreditTransactionWithNullAmount() {
        LoadRequest loadRequest = new LoadRequest();
        String messageId = UUID.randomUUID().toString();
        loadRequest.setUserId(USER_ID);
        loadRequest.setMessageId(messageId);
        webTestClient
                .put()
                .uri(uriBuilder -> creditUrl(uriBuilder, messageId))
                .bodyValue(loadRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message").isNotEmpty()
                .jsonPath("message").isEqualTo("transactionAmount must not be null");
    }

}
