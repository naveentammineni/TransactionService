package com.naveen.TransactionService.balance;

import com.naveen.TransactionService.model.CurrentTransaction;
import com.naveen.TransactionService.model.CurrentTransactionRepository;
import com.naveen.TransactionService.utils.BalanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService {

    @Autowired
    CurrentTransactionRepository currentTransactionRepository;

    public float getBalanceByUser(String userId) {

        List<CurrentTransaction> transactionList = currentTransactionRepository.findByUserId(userId);
        return BalanceUtil.findBalanceFromListOfTransactions(transactionList);

    }

}
