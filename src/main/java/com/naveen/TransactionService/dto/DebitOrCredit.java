package com.naveen.TransactionService.dto;

public enum DebitOrCredit {
    DEBIT, // Deducts funds from a user.
    CREDIT, // Adds funds to a user.
    SNAPSHOT //To indicate if the record type is a SNAPSHOT in the DB.
}
