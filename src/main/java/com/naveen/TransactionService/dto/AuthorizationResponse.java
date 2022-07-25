package com.naveen.TransactionService.dto;

import lombok.Data;

@Data
public class AuthorizationResponse {

    String userId;
    String messageId;
    ResponseCode responseCode;
    Amount balance;

}
