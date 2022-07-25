package com.naveen.TransactionService.dto;

import lombok.Data;

@Data
public class LoadResponse {

    String userId;
    String messageId;
    Amount balance;

}
