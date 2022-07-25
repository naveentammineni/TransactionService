package com.naveen.TransactionService.dto;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Min;

@Data
@Value(staticConstructor = "of")
public class Amount {

    @Min(1)
    String amount;

    @Min(1)
    String currency;

    DebitOrCredit debitOrCredit;

}
