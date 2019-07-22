package com.example.demo.repositories;

import com.example.demo.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAccountByAccountNumber(String accountNumber);
    List<Account> findAccountsByOwnerName(String ownerName);
    Account findAccountByAccountId(long accountId);
    List<Account> findAccountByIsVisible(boolean isVisible);
}