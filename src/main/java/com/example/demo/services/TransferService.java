package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface TransferService {

    List<Account> makeTransfer(String accountNumberFrom, String accountNumberTo, Double valueOfTransfer);

    List<Transfer> getAllTransfers();

    List<Transfer> getTransfersByAccountId(long accountId);

    void finishTransfers();

    List<Transfer> getTransfersOutByAccountId(long accountId);

    List<Transfer> getTransfersInByAccountId(long accountId);

    Transfer cancelTransfer(long transferId);
}