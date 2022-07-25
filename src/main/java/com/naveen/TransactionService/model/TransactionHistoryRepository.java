package com.naveen.TransactionService.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, TransactionId> {


}
