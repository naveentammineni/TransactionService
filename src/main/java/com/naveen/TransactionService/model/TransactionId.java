package com.naveen.TransactionService.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransactionId implements Serializable {

    String messageId;

    String userId;

}
