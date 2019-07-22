package com.example.demo.services;

import com.example.demo.entities.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    void addAccount(Account account);

    void updateAccount(long accountId, Account account);

    List<Account> getAllAccounts();

    Account findAccountByAccountNumber(String accountNumber);

    Account findAccountByAccountId(long accountId);

    void deleteAccountById(long accountId);

    List<Account> findAccountByOwnerName(String ownerName);
}
