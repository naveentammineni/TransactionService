package com.naveen.TransactionService.model;

import com.naveen.TransactionService.dto.DebitOrCredit;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "TransactionHistory")
@IdClass(TransactionId.class)
@Data
public class TransactionHistory {

    @Id
    String userId;

    @Id
    String messageId;

    float amount;

    String currency;

    Instant createdAt;

    DebitOrCredit debitOrCreditType;

}
