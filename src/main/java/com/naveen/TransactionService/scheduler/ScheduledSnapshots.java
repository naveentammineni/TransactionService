package com.naveen.TransactionService.scheduler;

import com.naveen.TransactionService.model.CurrentTransactionRepository;
import com.naveen.TransactionService.dto.DebitOrCredit;
import com.naveen.TransactionService.model.CurrentTransaction;
import com.naveen.TransactionService.utils.BalanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

/**
 * This is the scheduled job that runs every day at 1AM to consolidate all the transactions happened for a given user upto last day
 * and create a snapshot record in the current transactions' table with one record of the latest available amount in the account.
 */
@Component
@Slf4j
public class ScheduledSnapshots {


    @Autowired
    private CurrentTransactionRepository currentTransactionRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    public void computeAccountSnapshot() {

        List<CurrentTransaction> currentTransactionList = currentTransactionRepository.findAll();
        Map<String, List<CurrentTransaction>> transactionsMap = new HashMap<>();
        //Collect the records of each user and the transactions of the last day.
        for (CurrentTransaction currentTransaction : currentTransactionList) {
            transactionsMap.compute(currentTransaction.getUserId(), (key, value) -> {
                if (value == null) {
                    List.of(currentTransaction);
                } else {
                    value.add(currentTransaction);
                }
                return value;
            });
        }

        //Iterate over each user and create the snapshot by computing the final balance amount for the last day.
        for(Map.Entry<String, List<CurrentTransaction>> mapEntry: transactionsMap.entrySet()) {
            CurrentTransaction newTransaction = new CurrentTransaction();
            newTransaction.setDebitOrCreditType(DebitOrCredit.SNAPSHOT);
            newTransaction.setMessageId(UUID.randomUUID().toString());
            newTransaction.setCreatedAt(Instant.now());
            newTransaction.setCurrency(mapEntry.getValue().get(0).getCurrency());
            float amount = BalanceUtil.findBalanceFromListOfTransactions(currentTransactionList);
            newTransaction.setAmount(amount);
            currentTransactionRepository.deleteByUserId(mapEntry.getKey());
            currentTransactionRepository.save(newTransaction);
        }

    }

}
