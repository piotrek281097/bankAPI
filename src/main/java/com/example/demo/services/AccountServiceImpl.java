package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {


    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void addAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public void deleteAccountByNumber(String accountNumber) {

        Optional<Account> optionalAccount = accountRepository.findAccountByAccountNumber(accountNumber);

        if (optionalAccount.isPresent()) {
            accountRepository.delete(optionalAccount.get());
        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");

        }
    }

    @Override
    public void updateAccount(String accountNumber, Account account) {

        Optional<Account> optionalAccount = accountRepository.findAccountByAccountNumber(accountNumber);

        if (optionalAccount.isPresent()) {
            optionalAccount.get().setAccountNumber(account.getAccountNumber());
            optionalAccount.get().setCurrency(account.getCurrency());
            optionalAccount.get().setMoney(account.getMoney());
            optionalAccount.get().setOwnerName(account.getOwnerName());

            addAccount(optionalAccount.get());

        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findAccountByAccountNumber(String accountNumber) {
        Optional<Account> optionalAccount = accountRepository.findAccountByAccountNumber(accountNumber);

        if (optionalAccount.isPresent()) {
            return optionalAccount;
        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }
}
