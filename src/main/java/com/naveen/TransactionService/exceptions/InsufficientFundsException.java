package com.naveen.TransactionService.exceptions;


import com.naveen.TransactionService.dto.AuthorizationResponse;

public class InsufficientFundsException extends RuntimeException {

    AuthorizationResponse authorizationResponse;
    public InsufficientFundsException() {
        super("Insufficient funds for the transaction.");
    }

    public InsufficientFundsException(String message, AuthorizationResponse authorizationResponse) {
        super(message);
        this.authorizationResponse = authorizationResponse;
    }



}
