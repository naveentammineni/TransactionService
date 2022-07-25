package com.naveen.TransactionService.credit;

import com.naveen.TransactionService.balance.BalanceService;
import com.naveen.TransactionService.dto.*;
import com.naveen.TransactionService.model.CurrentTransaction;
import com.naveen.TransactionService.model.CurrentTransactionRepository;
import com.naveen.TransactionService.model.TransactionHistory;
import com.naveen.TransactionService.model.TransactionHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class CreditService {

    @Autowired
    CurrentTransactionRepository currentTransactionRepository;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    BalanceService balanceService;

    public LoadResponse createCreditTransaction(LoadRequest loadRequest) {

        LoadResponse loadResponse = new LoadResponse();
        loadResponse.setMessageId(loadRequest.getMessageId());
        loadResponse.setUserId(loadRequest.getUserId());

        saveToCurrentTransactionRepository(loadRequest);
        saveToTransactionHistoryRepository(loadRequest);

        float balance = balanceService.getBalanceByUser(loadRequest.getUserId());
        loadResponse.setBalance(
                Amount.of(balance + "", loadRequest.getTransactionAmount().getCurrency(), DebitOrCredit.CREDIT)
        );
        return loadResponse;

    }

    private void saveToCurrentTransactionRepository(LoadRequest loadRequest) {
        CurrentTransaction currentTransaction = new CurrentTransaction();
        currentTransaction.setCreatedAt(Instant.now());
        currentTransaction.setAmount(Float.parseFloat(loadRequest.getTransactionAmount().getAmount()));
        currentTransaction.setCurrency(loadRequest.getTransactionAmount().getCurrency());
        currentTransaction.setDebitOrCreditType(DebitOrCredit.CREDIT);
        currentTransaction.setMessageId(loadRequest.getMessageId());
        currentTransaction.setUserId(loadRequest.getUserId());
        currentTransactionRepository.save(currentTransaction);
    }

    private void saveToTransactionHistoryRepository(LoadRequest loadRequest) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setAmount(Float.parseFloat(loadRequest.getTransactionAmount().getAmount()));
        transactionHistory.setCurrency(loadRequest.getTransactionAmount().getCurrency());
        transactionHistory.setDebitOrCreditType(DebitOrCredit.CREDIT);
        transactionHistory.setUserId(loadRequest.getUserId());
        transactionHistory.setMessageId(loadRequest.getMessageId());
        transactionHistory.setCreatedAt(Instant.now());
        transactionHistoryRepository.save(transactionHistory);
    }

}
