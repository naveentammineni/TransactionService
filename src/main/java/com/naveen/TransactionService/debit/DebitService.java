package com.naveen.TransactionService.debit;

import com.naveen.TransactionService.balance.BalanceService;
import com.naveen.TransactionService.dto.*;
import com.naveen.TransactionService.model.CurrentTransactionRepository;
import com.naveen.TransactionService.exceptions.InsufficientFundsException;
import com.naveen.TransactionService.model.CurrentTransaction;
import com.naveen.TransactionService.model.TransactionHistory;
import com.naveen.TransactionService.model.TransactionHistoryRepository;
import com.naveen.TransactionService.utils.BalanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class DebitService {

    @Autowired
    CurrentTransactionRepository currentTransactionRepository;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    BalanceService balanceService;

    public AuthorizationResponse debitTransaction(AuthorizationRequest authorizationRequest) {


        AuthorizationResponse authorizationResponse = new AuthorizationResponse();

        authorizationResponse.setMessageId(authorizationRequest.getMessageId());
        authorizationResponse.setUserId(authorizationRequest.getUserId());

        if(checkIfDebitPossible(authorizationRequest)) {
            saveToCurrentTransactionRepository(authorizationRequest);
            saveToTransactionHistoryRepository(authorizationRequest);

            float balance = balanceService.getBalanceByUser(authorizationRequest.getUserId());
            authorizationResponse.setResponseCode(ResponseCode.APPROVED);
            authorizationResponse.setBalance(
                    Amount.of(balance+"", authorizationRequest.getTransactionAmount().getCurrency(), DebitOrCredit.CREDIT)
            );
            return authorizationResponse;
        } else {
            log.error("Debit transaction not allowed, insufficient funds.");
            authorizationResponse.setResponseCode(ResponseCode.DECLINED);
            authorizationResponse.setBalance(null);
            throw new InsufficientFundsException("Debit transaction not allowed, insufficient funds.", authorizationResponse);
        }

    }

    private void saveToTransactionHistoryRepository(AuthorizationRequest authorizationRequest) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setAmount(Float.parseFloat(authorizationRequest.getTransactionAmount().getAmount()));
        transactionHistory.setCurrency(authorizationRequest.getTransactionAmount().getCurrency());
        transactionHistory.setDebitOrCreditType(DebitOrCredit.DEBIT);
        transactionHistory.setUserId(authorizationRequest.getUserId());
        transactionHistory.setMessageId(authorizationRequest.getMessageId());
        transactionHistory.setCreatedAt(Instant.now());
        transactionHistoryRepository.save(transactionHistory);
    }

    private void saveToCurrentTransactionRepository(AuthorizationRequest authorizationRequest) {
        CurrentTransaction currentTransaction = new CurrentTransaction();
        currentTransaction.setCreatedAt(Instant.now());
        currentTransaction.setAmount(Float.parseFloat(authorizationRequest.getTransactionAmount().getAmount()));
        currentTransaction.setCurrency(authorizationRequest.getTransactionAmount().getCurrency());
        currentTransaction.setDebitOrCreditType(DebitOrCredit.DEBIT);
        currentTransaction.setMessageId(authorizationRequest.getMessageId());
        currentTransaction.setUserId(authorizationRequest.getUserId());
        currentTransactionRepository.save(currentTransaction);
    }

    private boolean checkIfDebitPossible(AuthorizationRequest authorizationRequest) {

        List<CurrentTransaction> currentTransactionList =
                currentTransactionRepository.findByUserId(authorizationRequest.getUserId());
        float totalSum = BalanceUtil.findBalanceFromListOfTransactions(currentTransactionList);
        return totalSum >= Float.parseFloat(authorizationRequest.getTransactionAmount().getAmount());

    }

}
