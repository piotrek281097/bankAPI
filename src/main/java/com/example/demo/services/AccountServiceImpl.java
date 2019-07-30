package com.example.demo.services;

import com.example.demo.DTOs.ExternalAccountDto;
import com.example.demo.Utils.CheckingMethodsObject;
import com.example.demo.Utils.MathematicalMethodsObject;
import com.example.demo.entities.Account;
import com.example.demo.exceptions.*;
import com.example.demo.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.client.RestTemplate;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
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
                CheckingMethodsObject.checkingIfCurrencyExists(account);
                account.setMoney(MathematicalMethodsObject.roundValue(account.getMoney()));
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

        CheckingMethodsObject.checkingIfAccountExists(account);

        account.setVisible(false);
        updateAccount(accountId, account);
    }

    @Override
    public void updateAccount(long accountId, Account account) {
        Account accountToSave = accountRepository.findAccountByAccountId(accountId);

        CheckingMethodsObject.checkingIfAccountExists(account);
        CheckingMethodsObject.checkingIfCurrencyExists(account);

        accountToSave.setMoney(MathematicalMethodsObject.roundValue(account.getMoney()));
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
    public List<ExternalAccountDto> getAllExternalsAccounts() {
        List<ExternalAccountDto> externalsAccounts = new ArrayList<>();
        ExternalAccountDto[] externalAccountDtos;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://restapi97.herokuapp.com/api/accounts";
            ResponseEntity<ExternalAccountDto[]> response = restTemplate.getForEntity(url, ExternalAccountDto[].class);
            externalAccountDtos = response.getBody();

            if(externalAccountDtos != null) {
                return new ArrayList<>(Arrays.asList(externalAccountDtos));
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return externalsAccounts;
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findAccountByAccountNumber(accountNumber);

        CheckingMethodsObject.checkingIfAccountExists(account);

        return account;
    }

    @Override
    public Account findAccountByAccountId(long accountId) {
        Account account = accountRepository.findAccountByAccountId(accountId);

        CheckingMethodsObject.checkingIfAccountExists(account);

        return account;
    }

    @Override
    public List<Account> findAccountByOwnerName(String accountNumber) {
        List<Account> accountsFoundByName = accountRepository.findAccountsByOwnerName(accountNumber);

        CheckingMethodsObject.checkingIfAccountsFoundByNameExist(accountsFoundByName);

        return accountsFoundByName;
    }
}
