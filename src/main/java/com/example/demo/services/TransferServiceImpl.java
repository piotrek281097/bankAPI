package com.example.demo.services;
import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.enums.TransferStatus;
import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import com.example.demo.exceptions.*;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;

    @Autowired
    public TransferServiceImpl(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Override
    public List<Account> makeTransfer(String accountNumberFrom, String accountNumberTo, Double valueOfTransfer) {
        if(!accountNumberFrom.equals(accountNumberTo)) {

            valueOfTransfer = roundValue(valueOfTransfer);
            Account firstAccount = accountRepository.findAccountByAccountNumber(accountNumberFrom);
            Account secondAccount = accountRepository.findAccountByAccountNumber(accountNumberTo);

            Double newMoneyAmountToFirstAccount, moneyTransferAmountTo;

            if(firstAccount == null) {
                throw new AccountDoesNotExistException("Rachunek z ktorego mial byc przelew nie istnieje");
            }
            else if(secondAccount == null) {
                throw new AccountDoesNotExistException("Rachunek na ktory mial byc przelew nie istnieje");
            }
            else {
                if(firstAccount.getCurrency().equals(secondAccount.getCurrency())) {
                    newMoneyAmountToFirstAccount = firstAccount.getMoney() - valueOfTransfer;
                    moneyTransferAmountTo = valueOfTransfer;
                }
                else {
                    moneyTransferAmountTo = convertCurrencies(firstAccount.getCurrency(), secondAccount.getCurrency(), valueOfTransfer );
                    newMoneyAmountToFirstAccount = firstAccount.getMoney() - valueOfTransfer;
                }

                List<Account> updatedAccountsList = new ArrayList<>();

                if (newMoneyAmountToFirstAccount > 0) {
                    firstAccount.setMoney(newMoneyAmountToFirstAccount);
                }
                else {
                    throw new NotEnoughMoneyToMakeTransferException("Za malo pieniedzy");
                }

                updatedAccountsList.add(firstAccount);
                updatedAccountsList.add(secondAccount);

                accountRepository.save(firstAccount);

                addTransfer(new Transfer(firstAccount.getAccountNumber(), secondAccount.getAccountNumber(), moneyTransferAmountTo, secondAccount.getCurrency(),
                        LocalDateTime.now(), null, TransferStatus.OPENED));

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

    @Override
    public List<Transfer> getTransfersOutByAccountNumber(String accountNumber) {
        List<Transfer> transfers = transferRepository.findByFirstAccountNumber(accountNumber);

        return transfers;
    }

    @Override
    public List<Transfer> getTransfersInByAccountNumber(String accountNumber) {
        List<Transfer> transfers = transferRepository.findBySecondAccountNumber(accountNumber);

        return transfers;
    }


    private Double convertCurrencies(String currency1, String currency2, Double valueOfTransfer) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangeratesapi.io/latest?base=" + currency1;
            ResponseEntity<CurrencyDto> response = restTemplate.getForEntity(url, CurrencyDto.class);
            CurrencyDto currencyDto = response.getBody();

            return roundValue(valueOfTransfer * currencyDto.getRates().get(currency2));
        }
        catch (NullPointerException ex) {
            throw new CurrencyIsNotAvailableException("Blad w konwersji walut");
        }
        catch (ResourceAccessException ex) {
           throw new ConnectionException("Blad w polaczeniu z API");
        }
    }

    @Override
    public void finishTransfers() {
        List<Transfer> transfers = getAllTransfers();

        transfers.stream()
                .filter(x -> x.getTransferStatus() == TransferStatus.OPENED)
                .forEach(x -> {
                    Account secondAccount = accountRepository.findAccountByAccountNumber(x.getSecondAccountNumber());
                    secondAccount.setMoney(roundValue(secondAccount.getMoney() + x.getMoney()));
                    accountRepository.save(secondAccount);

                    x.setTransferStatus(TransferStatus.FINISHED);
                    x.setDataFinishTransfer(LocalDateTime.now());
                    transferRepository.save(x);
                });
    }

    private static double roundValue(Double value) {
        String newValue = new DecimalFormat("##.##").format(value);
        newValue = newValue.replace(",", ".");

        return Double.parseDouble(newValue);
    }

   @Override
    public Transfer cancelTransfer(long transferId) {
        Transfer canceledTransfer = transferRepository.findByTransferId(transferId);
        if(canceledTransfer.getTransferStatus() == TransferStatus.OPENED) {
            canceledTransfer.setTransferStatus(TransferStatus.CANCELED);
            transferRepository.save(canceledTransfer);
        }
        else if(canceledTransfer.getTransferStatus() == TransferStatus.FINISHED) {
            throw new TransferCantBeCanceledException("Transfer już zakończony, nie można anulować");
        }

        return canceledTransfer;
    }
}
