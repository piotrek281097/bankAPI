package com.example.demo.services;

import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.entities.Account;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.AccountWithThisNumberAlreadyExistsException;
import com.example.demo.exceptions.ConnectionException;
import com.example.demo.exceptions.CurrencyIsNotAvailableException;
import com.example.demo.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void addAccount(Account account) {

        try {
            findAccountByAccountNumber(account.getAccountNumber());
                throw new AccountWithThisNumberAlreadyExistsException("Konto o takim numerze już istnieje");
        } catch (AccountDoesNotExistException exc) {

            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = "https://api.exchangeratesapi.io/latest?base=" + account.getCurrency();
                ResponseEntity<CurrencyDto> response = restTemplate.getForEntity(url, CurrencyDto.class);
                CurrencyDto currencyDto = response.getBody();

                currencyDto.getRates().get(account.getCurrency());
            } catch (ResourceAccessException ex) {
                throw new ConnectionException("Blad w polaczeniu z API");
            } catch (Exception ex) {
                throw new CurrencyIsNotAvailableException("Blad z waluta");
            }

            account.setMoney(roundValue(account.getMoney()));
            accountRepository.save(account);
        }


    }

    @Override
    public void deleteAccountById(long accountId) {
        Account account = accountRepository.findAccountByAccountId(accountId);

        if (account != null) {
            accountRepository.delete(account);
        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");

        }
    }

    @Override
    public void updateAccount(long accountId, Account account) {
        Account accountToSave = accountRepository.findAccountByAccountId(accountId);

        if (accountToSave != null ) {
            if(account.getMoney() != null) {
                accountToSave.setMoney(roundValue(account.getMoney()));
            }
            if(account.getCurrency() != null) {
                accountToSave.setCurrency(account.getCurrency());
            }
            if(account.getOwnerName() != null) {
                accountToSave.setOwnerName(account.getOwnerName());
            }

            accountRepository.save(accountToSave);
        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findAccountByAccountNumber(accountNumber);

        if (account != null) {
            return account;
        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }

    @Override
    public Account findAccountByAccountId(long accountId) {
        Account account = accountRepository.findAccountByAccountId(accountId);

        if (account != null) {
            return account;
        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }

    @Override
    public List<Account> findAccountByOwnerName(String accountNumber) {
        List<Account> accountsFoundByName = accountRepository.findAccountsByOwnerName(accountNumber);

        if (accountsFoundByName.size() != 0) {
            return accountsFoundByName;
        }
        else {
            throw new AccountDoesNotExistException("Rachunek o takim nazwisku nie istnieje!");
        }
    }

    public static double roundValue(Double value) {
        String newValue = new DecimalFormat("##.##").format(value);
        newValue = newValue.replace(",", ".");

        return Double.parseDouble(newValue);
    }
}
