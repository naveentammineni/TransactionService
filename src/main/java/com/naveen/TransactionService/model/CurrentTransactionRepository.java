package com.naveen.TransactionService.model;

import com.naveen.TransactionService.model.CurrentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrentTransactionRepository extends JpaRepository<CurrentTransaction, String> {

    List<CurrentTransaction> findByUserId(String userId);

    void deleteByUserId(String userId);

}
