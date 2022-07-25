package com.naveen.TransactionService.utils;

import com.naveen.TransactionService.dto.DebitOrCredit;
import com.naveen.TransactionService.model.CurrentTransaction;

import java.util.List;

public class BalanceUtil {

    public static float findBalanceFromListOfTransactions(List<CurrentTransaction> transactionList) {
        float amount = 0;
        for(CurrentTransaction currentTransaction : transactionList) {
            if(currentTransaction.getDebitOrCreditType() == DebitOrCredit.DEBIT) {
                amount -= currentTransaction.getAmount();
            } else {
                amount += currentTransaction.getAmount();
            }
        }
        return amount;
    }
}
