package com.example.demo.services;

import com.example.demo.Utils.MathematicalMethodsObject;
import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import com.example.demo.enums.TransferStatus;
import com.example.demo.exceptions.AccountAlreadyDeletedException;
import com.example.demo.exceptions.NotEnoughMoneyToMakeTransferException;
import com.example.demo.exceptions.TransferCantBeCanceledException;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.TransferRepository;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TransferServiceImplTest {

    private TransferService transferService;
    private AccountRepository accountRepository;
    private TransferRepository transferRepository;

    private Account accountFromIsTransfer;
    private Account accountToIsTransfer;
    private Account account;


    private Transfer transfer, transfer2, transfer3, transfer4, transfer5;

    @Before
    public void setup() {
        double moneyTransfer = 100.00;
        Account account2;

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
                         .accountId(1)
                         .accountNumber("32345678901234567890123456")
                         .money(100.00)
                         .currency("EUR")
                         .ownerName("Owner3")
                         .isVisible(true)
                         .build();

        account2 = Account.builder()
                         .accountId(2)
                         .accountNumber("42345678901234567890123456")
                         .money(200.00)
                         .currency("EUR")
                         .ownerName("Owner4")
                         .isVisible(true)
                         .build();

       transfer = Transfer.builder()
                          .transferId(1)
                          .sendingAccount(accountFromIsTransfer)
                          .targetAccount(accountToIsTransfer)
                          .moneyBeforeConverting(moneyTransfer)
                          .money(MathematicalMethodsObject.convertCurrencies(accountFromIsTransfer.getCurrency(), accountToIsTransfer.getCurrency(), moneyTransfer))
                          .currency(accountToIsTransfer.getCurrency())
                          .dataOpenTransfer(LocalDateTime.now())
                          .dataFinishTransfer(null)
                          .transferStatus(TransferStatus.OPENED.getValue())
                          .build();


        transfer2 = Transfer.builder()
                            .transferId(2)
                            .sendingAccount(accountFromIsTransfer)
                            .targetAccount(accountToIsTransfer)
                            .moneyBeforeConverting(moneyTransfer)
                            .money(MathematicalMethodsObject.convertCurrencies(accountFromIsTransfer.getCurrency(), accountToIsTransfer.getCurrency(), moneyTransfer))
                            .currency(accountToIsTransfer.getCurrency())
                            .dataOpenTransfer(LocalDateTime.now())
                            .dataFinishTransfer(null)
                            .transferStatus(TransferStatus.OPENED.getValue())
                            .build();

        transfer3 = Transfer.builder()
                            .sendingAccount(account2)
                            .targetAccount(account)
                            .moneyBeforeConverting(30.00)
                            .money(30.00)
                            .currency(account.getCurrency())
                            .dataOpenTransfer(LocalDateTime.now())
                            .dataFinishTransfer(null)
                            .transferStatus(TransferStatus.OPENED.getValue())
                            .build();

        transfer4 = Transfer.builder()
                            .sendingAccount(account)
                            .targetAccount(account2)
                            .moneyBeforeConverting(40.00)
                            .money(40.00)
                            .currency(account2.getCurrency())
                            .dataOpenTransfer(LocalDateTime.now())
                            .dataFinishTransfer(null)
                            .transferStatus(TransferStatus.OPENED.getValue())
                            .build();

        transfer5 = Transfer.builder()
                            .sendingAccount(account2)
                            .targetAccount(account)
                            .moneyBeforeConverting(50.00)
                            .money(50.00)
                            .currency(account.getCurrency())
                            .dataOpenTransfer(LocalDateTime.now())
                            .dataFinishTransfer(null)
                            .transferStatus(TransferStatus.OPENED.getValue())
                            .build();

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
    public void testShouldReturnListOf2() {
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(transfer);
        transfers.add(transfer2);

        when(transferRepository.findAll()).thenReturn(transfers);

        List<Transfer> transfersFound = transferService.getAllTransfers();

        assertThat(transfersFound.size(), is(2));
    }

    @Test
    public void testShouldReturnListFoundTransfersByIdOfSize3() {
        List<Transfer> transfers = new ArrayList<>();
        List<Transfer> transfers2 = new ArrayList<>();
        transfers.add(transfer3);
        transfers2.add(transfer4);
        transfers2.add(transfer5);

        when(transferRepository.findBySendingAccountAccountId(account.getAccountId())).thenReturn(transfers);
        when(transferRepository.findByTargetAccountAccountId(account.getAccountId())).thenReturn(transfers2);

        List<Transfer> transfersFound = transferService.getTransfersByAccountId(account.getAccountId());

        assertThat(transfersFound.size(), is(3));
    }

    @Test
    public void testShouldReturnTransfersWithChangedStatusToCancelled() {
        when(transferRepository.findByTransferId(transfer.getTransferId())).thenReturn(transfer);
        when(accountRepository.findAccountByAccountNumber(transfer.getSendingAccount().getAccountNumber())).thenReturn(accountFromIsTransfer);

        transferService.cancelTransfer(transfer.getTransferId());

        assertThat(transfer.getTransferStatus(), is("CANCELED"));
    }

    @Test
    public void testShouldReturnTransfersWithChangedStatusToFinished() {
        List<Transfer> transfersOpened = new ArrayList<>();
        transfersOpened.add(transfer);
        transfersOpened.add(transfer2);

        when(accountRepository.findAccountByAccountNumber(transfer.getTargetAccount().getAccountNumber())).thenReturn(accountToIsTransfer);
        when(accountRepository.findAccountByAccountNumber(transfer2.getTargetAccount().getAccountNumber())).thenReturn(accountToIsTransfer);
        when(transferRepository.findByTransferId(transfersOpened.get(0).getTransferId())).thenReturn(transfersOpened.get(0));
        when(transferRepository.findByTransferId(transfersOpened.get(1).getTransferId())).thenReturn(transfersOpened.get(1));
        when(transferRepository.findByTransferStatus(TransferStatus.OPENED.getValue())).thenReturn(transfersOpened);

        transferService.finishTransfers();

        assertThat(transfer.getTransferStatus(), is("FINISHED"));
        assertThat(transfer2.getTransferStatus(), is("FINISHED"));
    }

    @Test(expected = TransferCantBeCanceledException.class)
    public void testShouldReturnTransferCantBeCanceledException() {
        transfer.setTransferStatus(TransferStatus.FINISHED.getValue());

        when(transferRepository.findByTransferId(transfer.getTransferId())).thenReturn(transfer);

        transferService.cancelTransfer(transfer.getTransferId());
    }

    @Test(expected = AccountAlreadyDeletedException.class)
    public void testShouldReturnAccountAlreadyDeletedException() {
        List<Transfer> transfersOpened = new ArrayList<>();
        transfersOpened.add(transfer);

        accountToIsTransfer.setVisible(false);

        when(transferRepository.findByTransferStatus(TransferStatus.OPENED.getValue())).thenReturn(transfersOpened);
        when(accountRepository.findAccountByAccountNumber(transfer.getTargetAccount().getAccountNumber())).thenReturn(accountToIsTransfer);
        when(transferRepository.findByTransferId(transfersOpened.get(0).getTransferId())).thenReturn(transfersOpened.get(0));
        when(accountRepository.findAccountByAccountNumber(transfer.getSendingAccount().getAccountNumber())).thenReturn(accountFromIsTransfer);

        transferService.finishTransfers();
    }

}