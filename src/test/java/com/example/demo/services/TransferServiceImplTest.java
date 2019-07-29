package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.entities.ExternalTransfer;
import com.example.demo.entities.Transfer;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.AccountWithThisNumberAlreadyExistsException;
import com.example.demo.exceptions.NotEnoughMoneyToMakeTransferException;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.ExternalTransferRepository;
import com.example.demo.repositories.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TransferServiceImplTest {

    private TransferService transferService;
    private AccountRepository accountRepository;
    private TransferRepository transferRepository;

    private Account accountFromIsTransfer;
    private Account accountToIsTransfer;
    private Account account, account2;

    private Transfer transfer;

    @Before
    public void setup() {
        accountRepository = mock(AccountRepository.class);
        transferRepository = mock(TransferRepository.class);

        accountFromIsTransfer = Account.builder()
                                       .accountNumber("12345678901234567890123456")
                                       .money(100.00)
                                       .currency("USD")
                                       .ownerName("Owner1")
                                       .isVisible(true)
                                       .build();

        accountToIsTransfer = Account.builder()
                                       .accountNumber("22345678901234567890123456")
                                       .money(20.00)
                                       .currency("USD")
                                       .ownerName("Owner2")
                                       .isVisible(true)
                                       .build();

        account = Account.builder()
                         .accountNumber("32345678901234567890123456")
                         .money(200.00)
                         .currency("EUR")
                         .ownerName("Owner3")
                         .isVisible(true)
                         .build();


        transfer = new Transfer();
        transfer.setSendingAccount(accountFromIsTransfer);
        transfer.setTargetAccount(accountToIsTransfer);
        transfer.setMoney(100);
        transfer.setCurrency("EUR");
        transfer.setTransferStatus("OPENED");
        transfer.setDataOpenTransfer(LocalDateTime.now());
        transfer.setDataFinishTransfer(null);

        transferService = new TransferServiceImpl(accountRepository, transferRepository);
    }

    @Test(expected = NotEnoughMoneyToMakeTransferException.class)
    public void testShouldReturnNotEnoughMoneyToMakeTransferException() {

        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 200.00);
    }

    @Test
    public void testShouldReturnMethodSaveWasCalledTwice() {
        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 50.00);
        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 10.00);

        verify(accountRepository, times(2)).save(accountFromIsTransfer);
    }

    @Test
    public void testShouldReturnThatMoneyIsDeductedFromFirstAccount() {

        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 50.00);

        assertThat(accountFromIsTransfer.getMoney(), is(50.00));
    }

    @Test
    public void testShouldReturnThatMethodFindBySendingAccountAccountIdWasCalledOnce() {
        when(transferRepository.findBySendingAccountAccountId(account.getAccountId())).thenReturn(new ArrayList<>());
        transferService.getTransfersOutByAccountId(account.getAccountId());
        transferService.getTransfersOutByAccountId(account.getAccountId());

        verify(transferRepository, times(2)).findBySendingAccountAccountId(account.getAccountId());
    }

    @Test
    public void testShouldReturnThatMethodFindByTargetAccountAccountIdWasCalledOnce() {
        when(transferRepository.findByTargetAccountAccountId(account.getAccountId())).thenReturn(new ArrayList<>());
        transferService.getTransfersInByAccountId(account.getAccountId());

        verify(transferRepository, times(1)).findByTargetAccountAccountId(account.getAccountId());
    }

    @Test
    public void testShouldReturnThatMethodFindBySendingAndTargetAccountAccountIdWereCalledTwice() {
        when(transferRepository.findBySendingAccountAccountId(account.getAccountId())).thenReturn(new ArrayList<>());
        when(transferRepository.findByTargetAccountAccountId(account.getAccountId())).thenReturn(new ArrayList<>());

        transferService.getTransfersByAccountId(account.getAccountId());
        transferService.getTransfersByAccountId(account.getAccountId());

        verify(transferRepository, times(2)).findBySendingAccountAccountId(account.getAccountId());
        verify(transferRepository, times(2)).findByTargetAccountAccountId(account.getAccountId());
    }

    @Test
    public void testShouldReturnThatMethodSaveWasCalledOnce() {
        when(transferRepository.findByTransferId(transfer.getTransferId())).thenReturn(transfer);


        //verify(transferRepository, times(2)).findByTargetAccountAccountId(account.getAccountId());
    }

}