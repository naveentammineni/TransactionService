package com.naveen.TransactionService.model;

import com.naveen.TransactionService.dto.DebitOrCredit;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "CurrentTransactions")
@Data
@IdClass(TransactionId.class)
public class CurrentTransaction {

    @Id
    String messageId;

    @Id
    String userId;

    float amount;

    String currency;

    Instant createdAt;

    DebitOrCredit debitOrCreditType;

}
