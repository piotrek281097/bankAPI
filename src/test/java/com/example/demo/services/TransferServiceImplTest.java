package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.entities.ExternalTransfer;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TransferServiceImplTest {

    private TransferService transferService;
    private AccountService accountService;
    private ExternalTransferService externalTransferService;
    private AccountRepository accountRepository;
    private TransferRepository transferRepository;
    private ExternalTransferRepository externalTransferRepository;

    private Account accountFromIsTransfer;
    private Account accountToIsTransfer;
    private Account account, account2;
    private Account accountTheSameNumber;
    private Account updatedAccount;
    private ExternalTransfer externalTransfer;


    private String email;

    @Before
    public void setup() {
        accountRepository = mock(AccountRepository.class);
        transferRepository = mock(TransferRepository.class);
        externalTransferRepository = mock(ExternalTransferRepository.class);

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

        account2 = new Account();
        account2.setAccountNumber("52345678901234567890123456");
        account2.setMoney(500.00);
        account2.setCurrency("USD");
        account2.setOwnerName("Owner5");

        account = new Account();
        account.setAccountNumber("32345678901234567890123456");
        account.setMoney(200.00);
        account.setCurrency("EUR");
        account.setOwnerName("Owner3");

        accountTheSameNumber = new Account();
        accountTheSameNumber.setAccountNumber("32345678901234567890123456");
        accountTheSameNumber.setMoney(20.00);
        accountTheSameNumber.setCurrency("USD");
        accountTheSameNumber.setOwnerName("Owner4");

        updatedAccount = new Account();
        updatedAccount.setAccountNumber("42345678901234567890123456");
        updatedAccount.setMoney(100.00);
        updatedAccount.setCurrency("USD");
        updatedAccount.setOwnerName("Owner4");

        //String externalAccount, String toAccount, BigDecimal amount, String currency, String bankName
        externalTransfer = new ExternalTransfer("12345678901234567890123456", "12345678901234567890123451", BigDecimal.valueOf(20.0),
                "EUR", "myBank88");

        transferService = new TransferServiceImpl(accountRepository, transferRepository);
        accountService = new AccountServiceImpl(accountRepository);
        externalTransferService = new ExternalTransferServiceImpl(accountRepository, externalTransferRepository);

        email = "piotr.plecinski@wp.pl";
    }

    @Test(expected = NotEnoughMoneyToMakeTransferException.class)
    public void testShouldReturnNotEnoughMoneyToMakeTransferException() {

        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 200.00, email);
    }

    @Test(expected = AccountDoesNotExistException.class)
    public void testShouldReturnAccountDoesNotExistException() {

        when(accountRepository.findAccountByAccountNumber(any(String.class))).thenReturn(null);
        accountService.findAccountByAccountNumber("123456");
    }

    @Ignore
    @Test
    public void testShouldReturnMethodSaveWasCalledTwice() {
        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 50.00, email);
        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 10.00, email);

        verify(accountRepository, times(2)).save(accountFromIsTransfer);
    }


    @Test
    public void testShouldReturnThatUpdateWorksCorrectly() {
        when(accountRepository.findAccountByAccountId(account.getAccountId())).thenReturn(account);

        accountService.updateAccount(account.getAccountId(), updatedAccount);

        assertThat(account.getMoney(), is(100.00));
    }

    @Ignore
    @Test
    public void testShouldReturnThatMoneyIsDeductedFromFirstAccount() {

        when(accountRepository.findAccountByAccountNumber(accountFromIsTransfer.getAccountNumber())).thenReturn(accountFromIsTransfer);
        when(accountRepository.findAccountByAccountNumber(accountToIsTransfer.getAccountNumber())).thenReturn(accountToIsTransfer);

        transferService.makeTransfer(accountFromIsTransfer.getAccountNumber(), accountToIsTransfer.getAccountNumber(), 50.00, email);

        assertThat(accountFromIsTransfer.getMoney(), is(50.00));
    }

    @Test(expected = AccountWithThisNumberAlreadyExistsException.class)
    public void testShouldReturnAccountWithThisNumberAlreadyExistsException() {
        when(accountRepository.findAccountByAccountNumber(account.getAccountNumber())).thenReturn(account);
        accountService.addAccount(account);
        accountService.addAccount(accountTheSameNumber);
    }

    @Test
    public void testShouldReturnMethodSaveWasCalledOnceWhileAddingAccount() {
        when(accountRepository.findAccountByAccountNumber(account.getAccountNumber())).thenReturn(null);
        accountService.addAccount(account);

        verify(accountRepository, times(1)).save(account);
    }


    @Test
    public void testShouldReturnThatMoneyIsDeductedFromSendingAccount() {
        when(accountRepository.findAccountByAccountNumber(externalTransfer.getExternalAccount())).thenReturn(account);
        externalTransferService.makeExternalTransfer(externalTransfer);

        assertThat(account.getMoney(), is(180.00));
    }
}