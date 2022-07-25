package com.naveen.TransactionService.debit;

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
public class DebitControllerTests {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CurrentTransactionRepository currentTransactionRepository;

    private final String USER_ID = "10";

    public URI debitUrl(UriBuilder uriBuilder, String messageId) {
        return uriBuilder
                .path("/authorization/"+messageId)
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
    @DisplayName("Test the debit transaction, success")
    void testDebitTransaction() {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setTransactionAmount(
                Amount.of("100", "USD", DebitOrCredit.DEBIT)
        );
        authorizationRequest.setUserId(USER_ID);
        String messageId = UUID.randomUUID().toString();
        authorizationRequest.setMessageId(messageId);
        webTestClient
                .put()
                .uri(uriBuilder -> debitUrl(uriBuilder, messageId))
                .bodyValue(authorizationRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(AuthorizationResponse.class)
                .value(response -> {
                    assertThat(response.getResponseCode()).isEqualTo(ResponseCode.APPROVED);
                    assertThat(response.getBalance().getAmount()).isEqualTo("0.0");
                    assertThat(response.getBalance().getCurrency()).isEqualTo("USD");
                    assertThat(response.getMessageId()).isEqualTo(messageId);
                    assertThat(response.getUserId()).isEqualTo(USER_ID);
                });
    }

    @Test
    @DisplayName("Test the debit transaction with insufficient funds, error")
    void testDebitTransactionWithInsufficientFunds() {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setTransactionAmount(
                Amount.of("200", "USD", DebitOrCredit.DEBIT)
        );
        authorizationRequest.setUserId(USER_ID);
        String messageId = UUID.randomUUID().toString();
        authorizationRequest.setMessageId(messageId);
        webTestClient
                .put()
                .uri(uriBuilder -> debitUrl(uriBuilder, messageId))
                .bodyValue(authorizationRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message").isNotEmpty()
                .jsonPath("message").isEqualTo("Debit transaction not allowed, insufficient funds.");

    }

    @Test
    @DisplayName("Test the credit transaction with empty userId, error")
    void testCreditTransactionWithEmptyUserId() {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setTransactionAmount(
                Amount.of("200", "USD", DebitOrCredit.DEBIT)
        );
        String messageId = UUID.randomUUID().toString();
        authorizationRequest.setMessageId(messageId);
        webTestClient
                .put()
                .uri(uriBuilder -> debitUrl(uriBuilder, messageId))
                .bodyValue(authorizationRequest)
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
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setTransactionAmount(
                Amount.of("200", "USD", DebitOrCredit.DEBIT)
        );
        authorizationRequest.setUserId(USER_ID);
        String messageId = UUID.randomUUID().toString();
        webTestClient
                .put()
                .uri(uriBuilder -> debitUrl(uriBuilder, messageId))
                .bodyValue(authorizationRequest)
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
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setUserId(USER_ID);
        String messageId = UUID.randomUUID().toString();
        authorizationRequest.setMessageId(messageId);
        webTestClient
                .put()
                .uri(uriBuilder -> debitUrl(uriBuilder, messageId))
                .bodyValue(authorizationRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message").isNotEmpty()
                .jsonPath("message").isEqualTo("transactionAmount must not be null");
    }

}
