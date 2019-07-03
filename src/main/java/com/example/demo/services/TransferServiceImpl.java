package com.example.demo.services;
import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

            Account firstAccount = accountRepository.findAccountByAccountNumber(accountNumberFrom);
            Account secondAccount = accountRepository.findAccountByAccountNumber(accountNumberTo);

            Double newMoneyAmountToFirstAccount, newMoneyAmountToSecondAccount;

            if(firstAccount == null) {
                throw new AccountDoesNotExistException("Rachunek z ktorego mial byc przelew nie istnieje");
            }
            else if(secondAccount == null) {
                throw new AccountDoesNotExistException("Rachunek na ktory mial byc przelew nie istnieje");
            }
            else {
                newMoneyAmountToFirstAccount = firstAccount.getMoney() - valueOfTransfer;
                newMoneyAmountToSecondAccount = secondAccount.getMoney() + valueOfTransfer;

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

        if(transfers.size() == 0) {
            throw new AccountDoesNotExistException("Nie ma przelewow dla tego konta");
        }

        return transfers;
    }
}
