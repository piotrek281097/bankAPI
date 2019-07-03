package com.example.demo.services;

import com.example.demo.entities.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    void addAccount(Account account);
    void updateAccount(String accountNumber, Account account);
    List<Account> getAllAccounts();
    Account findAccountByAccountNumber(String accountNumber);
    void deleteAccountByNumber(String accountNumber);
}
