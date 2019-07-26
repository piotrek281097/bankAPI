package com.example.demo.Utils;

import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.entities.Account;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.ConnectionException;
import com.example.demo.exceptions.CurrencyIsNotAvailableException;
import com.example.demo.exceptions.NotEnoughMoneyToMakeTransferException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class CheckingMethodsObject {

    public static void checkingIfAccountExists(Account account) {
        if(account == null) {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }

    public static void checkingIfAccountsFoundByNameExist(List<Account> accounts) {
        if(accounts.size() == 0) {
            throw new AccountDoesNotExistException("Rachunek o takim nazwisku nie istnieje!");
        }
    }

    public static void checkingIfCurrencyExists(Account account) {
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

    public static void checkingIfThereIsEnoughMoneyToMakeTransfer(Account myAccount, Double moneyTransfer) {
        if ((myAccount.getMoney() - moneyTransfer) < 0) {
            throw new NotEnoughMoneyToMakeTransferException("Za malo pieniedzy");
        }
    }

    public static void checkingIfThereIsEnoughMoneyToMakeTransfer(Double newMoney) {
        if (newMoney < 0) {
            throw new NotEnoughMoneyToMakeTransferException("Za malo pieniedzy");
        }
    }

    public static void checkingIfAccountsExist(Account sendingAccount, Account targetAccount) {
        if (sendingAccount == null) {
            throw new AccountDoesNotExistException("Rachunek z ktorego mial byc przelew nie istnieje 1");
        } else if (targetAccount == null) {
            throw new AccountDoesNotExistException("Rachunek na ktory mial byc przelew nie istnieje 2");
        }
    }

}
