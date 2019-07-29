package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.entities.ExternalTransfer;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.ExternalTransferRepository;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExternalTransferServiceImplTest {

    private ExternalTransferService externalTransferService;
    private AccountRepository accountRepository;

    private Account account;
    private ExternalTransfer externalTransfer;
    private String email;

    @Before
    public void setup() {
        EmailService emailService;
        ExternalTransferRepository externalTransferRepository;

        accountRepository = mock(AccountRepository.class);
        externalTransferRepository = mock(ExternalTransferRepository.class);
        emailService = mock(EmailService.class);

        account = Account.builder()
                         .accountNumber("32345678901234567890123456")
                         .money(200.00)
                         .currency("EUR")
                         .ownerName("Owner1")
                         .isVisible(true)
                         .build();

        externalTransfer = ExternalTransfer.builder()
                                           .externalAccount("12345678901234567890123456")
                                           .toAccount("12345678901234567890123451")
                                           .amount(BigDecimal.valueOf(20.0))
                                           .currency("EUR")
                                           .bankName("myBank88")
                                           .build();

        externalTransferService = new ExternalTransferServiceImpl(accountRepository, externalTransferRepository, emailService);

        email = "piotr.plecinski@wp.pl";
    }

    @Test
    public void testShouldReturnThatMoneyIsDeductedFromSendingAccount() {
        when(accountRepository.findAccountByAccountNumber(externalTransfer.getExternalAccount())).thenReturn(account);
        externalTransferService.makeExternalTransfer(externalTransfer, email);

        assertThat(account.getMoney(), is(180.00));
    }

    @Test
    public void testShouldReturnThatMethodSaveWasCalledOnce() {
        when(accountRepository.findAccountByAccountNumber(externalTransfer.getExternalAccount())).thenReturn(account);
        externalTransferService.makeExternalTransfer(externalTransfer, email);

        verify(accountRepository, times(1)).save(account);
    }
}