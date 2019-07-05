package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.NotEnoughMoneyToMakeTransferException;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TransferServiceImplTest {

    private TransferService transferService;
    private AccountService accountService;

    private Account accountFromIsTransfer;
    private Account accountToIsTransfer;
    private Account account;
    private Account updatedAccount;

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;


    @Before
    public void setup() {
        accountRepository = mock(AccountRepository.class);
        transferRepository = mock(TransferRepository.class);

        accountFromIsTransfer = new Account();
        accountFromIsTransfer.setAccountNumber("12345678901234567890123456");
        accountFromIsTransfer.setMoney(100.00);
        accountFromIsTransfer.setCurrency("USD");
        accountFromIsTransfer.setOwnerName("Owner1");

        accountToIsTransfer = new Account();
        accountToIsTransfer.setAccountNumber("22345678901234567890123456");
        accountToIsTransfer.setMoney(20.00);
        accountToIsTransfer.setCurrency("USD");
        accountToIsTransfer.setOwnerName("Owner2");

        account = new Account();
        account.setAccountNumber("32345678901234567890123456");
        account.setMoney(200.00);
        account.setCurrency("USD");
        account.setOwnerName("Owner3");

        account = new Account();
        account.setAccountNumber("32345678901234567890123456");
        account.setMoney(200.00);
        account.setCurrency("USD");
        account.setOwnerName("Owner3");

        updatedAccount = new Account();
        updatedAccount.setAccountNumber("42345678901234567890123456");
        updatedAccount.setMoney(100.00);
        updatedAccount.setCurrency("USD");
        updatedAccount.setOwnerName("Owner4");

        transferService = new TransferServiceImpl(accountRepository, transferRepository);
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test(expected = NotEnoughMoneyToMakeTransferException.class)
    public void testShouldReturnNotEnoughMoneyToMakeTransferException() {

        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 200.00);
    }

    @Test(expected = AccountDoesNotExistException.class)
    public void testShouldReturnAccountDoesNotExistException() {

        when(accountRepository.findAccountByAccountNumber(any(String.class))).thenReturn(null);
        accountService.findAccountByAccountNumber("123456");
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
    public void testShouldReturnThatUpdateWorksCorrectly() {
        when(accountRepository.findAccountByAccountNumber(account.getAccountNumber())).thenReturn(account);

        accountService.updateAccount(account.getAccountNumber(), updatedAccount);

        assertThat(account.getMoney(), is(10.00));
    }

    @Test
    public void testShouldReturnThatMoneyIsDeductedFromFirstAccount() {

        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 50.00);

        assertThat(accountFromIsTransfer.getMoney(), is(50.00));
    }

}