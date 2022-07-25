package com.naveen.TransactionService.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AuthorizationRequest {

    @NotNull
    @NotBlank
    String userId;

    @NotNull
    String messageId;

    @NotNull
    Amount transactionAmount;

}
