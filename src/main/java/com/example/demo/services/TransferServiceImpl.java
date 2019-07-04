package com.example.demo.services;
import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.ConnectionException;
import com.example.demo.exceptions.CurrencyIsNotAvailableException;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public TransferServiceImpl(AccountRepository accountRepository, TransferRepository transferRepository, ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Account> makeTransfer(String accountNumberFrom, String accountNumberTo, Double valueOfTransfer) {

        if(!accountNumberFrom.equals(accountNumberTo)) {

            Account firstAccount = accountRepository.findAccountByAccountNumber(accountNumberFrom);
            Account secondAccount = accountRepository.findAccountByAccountNumber(accountNumberTo);

            Double newMoneyAmountToFirstAccount = Double.valueOf(0), newMoneyAmountToSecondAccount = Double.valueOf(0);

            if(firstAccount == null) {
                throw new AccountDoesNotExistException("Rachunek z ktorego mial byc przelew nie istnieje");
            }
            else if(secondAccount == null) {
                throw new AccountDoesNotExistException("Rachunek na ktory mial byc przelew nie istnieje");
            }
            else {
                if(firstAccount.getCurrency().equals(secondAccount.getCurrency())) {
                    newMoneyAmountToFirstAccount = firstAccount.getMoney() - valueOfTransfer;
                    newMoneyAmountToSecondAccount = secondAccount.getMoney() + valueOfTransfer;
                }
                else {
                    Double moneyTransferAmountTo = convertCurrencies(firstAccount.getCurrency(), secondAccount.getCurrency(), valueOfTransfer );

                    newMoneyAmountToFirstAccount = firstAccount.getMoney() - valueOfTransfer;
                    newMoneyAmountToSecondAccount = secondAccount.getMoney() + moneyTransferAmountTo;

                    System.out.println("TRANSFER MONEY: " + moneyTransferAmountTo);
                }



                List<Account> updatedAccountsList = new ArrayList<>();

                if (newMoneyAmountToFirstAccount > 0) {
                    firstAccount.setMoney(newMoneyAmountToFirstAccount);
                    secondAccount.setMoney(newMoneyAmountToSecondAccount);
                }

                updatedAccountsList.add(firstAccount);
                updatedAccountsList.add(secondAccount);

                accountRepository.save(firstAccount);
                accountRepository.save(secondAccount);

                addTransfer(new Transfer(firstAccount.getAccountNumber(), secondAccount.getAccountNumber(), valueOfTransfer));

                return updatedAccountsList;
            }
        }

        else return Collections.emptyList();
    }

    @Override
    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    @Override
    public void addTransfer(Transfer transfer) {
        transferRepository.save(transfer);
    }

    @Override
    public List<Transfer> getTransfersByAccountNumber(String accountNumber) {

        List<Transfer> transfersFrom = transferRepository.findByFirstAccountNumber(accountNumber);
        List<Transfer> transfersTo = transferRepository.findBySecondAccountNumber(accountNumber);

        List<Transfer> transfers = new ArrayList<>();
        transfers.addAll(transfersFrom);
        transfers.addAll(transfersTo);

        return transfers;
    }


    public Double convertCurrencies(String currency1, String currency2, Double valueOfTransfer) {

        try {

            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangeratesapi.io/latest?base=" + currency1;
            ResponseEntity<CurrencyDto> response = restTemplate.getForEntity(url, CurrencyDto.class);
            CurrencyDto currencyDto = response.getBody();

            double result = valueOfTransfer * currencyDto.getRates().get(currency2);

            return result;
        }
        catch (NullPointerException ex) {
            throw new CurrencyIsNotAvailableException("Blad w konwersji walut");
        }
        catch (ResourceAccessException ex) {
           throw new ConnectionException("Blad w polaczeniu z API");
        }
    }

}
