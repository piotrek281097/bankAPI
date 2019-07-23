package com.example.demo.services;

import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.entities.Account;
import com.example.demo.exceptions.*;
import com.example.demo.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import javax.validation.ConstraintViolationException;
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
                checkingIfCurrencyExists(account);
                account.setMoney(roundValue(account.getMoney()));
                accountRepository.save(account);
            }
            catch (ConstraintViolationException | IllegalArgumentException ex) {
                throw new WrongDataException("Błędne dane");
            }
        }
    }

    @Override
    public void deleteAccountById(long accountId) {
        Account account = accountRepository.findAccountByAccountId(accountId);

        checkingIfAccountExists(account);

        account.setVisible(false);
        updateAccount(accountId, account);
    }

    @Override
    public void updateAccount(long accountId, Account account) {
        Account accountToSave = accountRepository.findAccountByAccountId(accountId);

        checkingIfAccountExists(account);
        checkingIfCurrencyExists(account);

        accountToSave.setMoney(roundValue(account.getMoney()));
        accountToSave.setCurrency(account.getCurrency());
        accountToSave.setOwnerName(account.getOwnerName());

        try {
            accountRepository.save(accountToSave);
        }
        catch (TransactionSystemException ex) {
            throw new WrongDataException("Błędne dane");
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAccountByIsVisible(true);
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findAccountByAccountNumber(accountNumber);

        checkingIfAccountExists(account);

        return account;
    }

    @Override
    public Account findAccountByAccountId(long accountId) {
        Account account = accountRepository.findAccountByAccountId(accountId);

        checkingIfAccountExists(account);

        return account;
    }

    @Override
    public List<Account> findAccountByOwnerName(String accountNumber) {
        List<Account> accountsFoundByName = accountRepository.findAccountsByOwnerName(accountNumber);

        checkingIfAccountsFoundByNameExist(accountsFoundByName);

        return accountsFoundByName;
    }

    private static double roundValue(Double value) {
        String newValue = new DecimalFormat("##.##").format(value);
        newValue = newValue.replace(",", ".");

        return Double.parseDouble(newValue);
    }

    private void checkingIfCurrencyExists(Account account) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangeratesapi.io/latest?base=" + account.getCurrency();
            ResponseEntity<CurrencyDto> response = restTemplate.getForEntity(url, CurrencyDto.class);
            CurrencyDto currencyDto = response.getBody();

            if (currencyDto != null) {
                currencyDto.getRates().get(account.getCurrency());
            }
        } catch (NullPointerException ex) {
            throw new CurrencyIsNotAvailableException("Nie znaleziono takiej waluty");
        } catch (ResourceAccessException ex) {
            throw new ConnectionException("Blad w polaczeniu z API");
        } catch (HttpClientErrorException ex) {
            throw new CurrencyIsNotAvailableException("Błędne dane");
        }
    }

    private void checkingIfAccountExists(Account account) {
        if(account == null) {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }

    private void checkingIfAccountsFoundByNameExist(List<Account> accounts) {
        if(accounts.size() == 0) {
            throw new AccountDoesNotExistException("Rachunek o takim nazwisku nie istnieje!");
        }
    }


}
