package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface TransferService {

    List<Account> makeTransfer(String accountNumberFrom, String accountNumberTo, Double valueOfTransfer);
    List<Transfer> getAllTransfers();
    void addTransfer(Transfer transfer);
    List<Transfer> getTransfersByAccountNumber(String accountNumber);
    void finishTransfers();
    List<Transfer> getTransfersOutByAccountNumber(String accountNumber);
    List<Transfer> getTransfersInByAccountNumber(String accountNumber);
    Transfer cancelTransfer(long transferId);
}