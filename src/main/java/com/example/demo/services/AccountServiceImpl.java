package com.example.demo.services;

import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.entities.Account;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.ConnectionException;
import com.example.demo.exceptions.CurrencyIsNotAvailableException;
import com.example.demo.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

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
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangeratesapi.io/latest?base=" + account.getCurrency();
            ResponseEntity<CurrencyDto> response = restTemplate.getForEntity(url, CurrencyDto.class);
            CurrencyDto currencyDto = response.getBody();

            currencyDto.getRates().get(account.getCurrency());
        }
        catch (NullPointerException ex) {
            throw new CurrencyIsNotAvailableException("Blad w konwersji walut");
        }
        catch (ResourceAccessException ex) {
            throw new ConnectionException("Blad w polaczeniu z API");
        }

        accountRepository.save(account);
    }

    @Override
    public void deleteAccountByNumber(String accountNumber) {
        Account account = accountRepository.findAccountByAccountNumber(accountNumber);

        if (account != null) {
            accountRepository.delete(account);
        }
        else {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");

        }
    }

    @Override
    public void updateAccount(String accountNumber, Account account) {
        Account accountToSave = accountRepository.findAccountByAccountNumber(accountNumber);

        if (accountToSave != null ) {
            if(account.getMoney() != null) {
                accountToSave.setMoney(account.getMoney());
            }
            if(account.getCurrency() != null) {
                accountToSave.setCurrency(account.getCurrency());
            }
            if(account.getOwnerName() != null) {
                accountToSave.setOwnerName(account.getOwnerName());
            }

            addAccount(accountToSave);
            //accountRepository.save(accountToSave);
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
}
