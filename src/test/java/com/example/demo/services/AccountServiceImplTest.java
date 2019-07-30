package com.example.demo.services;

import com.example.demo.DTOs.ExternalAccountDto;
import com.example.demo.entities.Account;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.AccountWithThisNumberAlreadyExistsException;
import com.example.demo.exceptions.WrongDataException;
import com.example.demo.repositories.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class AccountServiceImplTest {

    private AccountService accountService;
    private AccountRepository accountRepository;

    private Account account, account2;
    private Account accountTheSameNumber;
    private Account updatedAccount;
    private List<Account> emptyList = new ArrayList<>();

    @Before
    public void setup() {
        accountRepository = mock(AccountRepository.class);

        account = Account.builder()
                         .accountNumber("32345678901234567890123456")
                         .money(200.00)
                         .currency("EUR")
                         .ownerName("Owner1")
                         .isVisible(true)
                         .build();

        account2 = Account.builder()
                          .accountNumber("22345678901234567890123456")
                          .money(500.00)
                          .currency("EUR")
                          .ownerName("Owner2")
                          .isVisible(true)
                          .build();

        accountTheSameNumber = Account.builder()
                                      .accountNumber("32345678901234567890123456")
                                      .money(100.00)
                                      .currency("USD")
                                      .ownerName("Owner3")
                                      .isVisible(true)
                                      .build();

        updatedAccount = Account.builder()
                                .accountNumber("42345678901234567890123456")
                                .money(100.00)
                                .currency("USD")
                                .ownerName("Owner4")
                                .isVisible(true)
                                .build();

        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    public void testShouldReturnThatUpdateWorksCorrectly() {
        when(accountRepository.findAccountByAccountId(account.getAccountId())).thenReturn(account);

        accountService.updateAccount(account.getAccountId(), updatedAccount);

        assertThat(account.getMoney(), is(100.00));
    }

    @Test
    public void testShouldReturnThatDeleteChangedStatusVisibleToFalse() {
        when(accountRepository.findAccountByAccountId(account.getAccountId())).thenReturn(account);

        accountService.deleteAccountById(account.getAccountId());

        assertThat(account.isVisible(), is(false));
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

    @Test(expected = AccountDoesNotExistException.class)
    public void testShouldReturnAccountDoesNotExistException() {
        when(accountRepository.findAccountByAccountNumber(any(String.class))).thenReturn(null);

        accountService.findAccountByAccountNumber("123456");
    }

    @Test
    public void testShouldReturnMethodSaveWasCalledOnceWhileDeletingById() {
        when(accountRepository.findAccountByAccountId(account.getAccountId())).thenReturn(account);

        accountService.deleteAccountById(account.getAccountId());

        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testShouldReturnMethodFindAccountByAccountIdWasCalledTwice() {
        when(accountRepository.findAccountByAccountId(account.getAccountId())).thenReturn(account);

        accountService.findAccountByAccountId(account.getAccountId());
        accountService.findAccountByAccountId(account.getAccountId());

        verify(accountRepository, times(2)).findAccountByAccountId(account.getAccountId());
    }

    @Test(expected = AccountDoesNotExistException.class)
    public void testShouldReturnAccountDoesNotExistExceptionWhileSearchingByOwnerName() {
        when(accountRepository.findAccountsByOwnerName(any(String.class))).thenReturn(emptyList);
        accountService.findAccountByOwnerName(account.getOwnerName());
    }

    @Test(expected = WrongDataException.class)
    public void testShouldReturnWrongDataExceptionWhenAccountRepositoryThrowsConstraintViolationException() {
        when(accountRepository.findAccountByAccountNumber(account.getAccountNumber())).thenReturn(null);
        when(accountRepository.save(account)).thenThrow(new ConstraintViolationException(new HashSet<>()));

        accountService.addAccount(account);
    }

    @Test(expected = WrongDataException.class)
    public void testShouldReturnWrongDataExceptionWhenAccountRepositoryThrowsIllegalArgumentException() {
        when(accountRepository.findAccountByAccountNumber(account.getAccountNumber())).thenReturn(null);
        when(accountRepository.save(account)).thenThrow(new IllegalArgumentException());

        accountService.addAccount(account);
    }

    @Test(expected = WrongDataException.class)
    public void testShouldReturnWrongDataExceptionWhenAccountRepositoryThrowsTransactionSystemException() {
        when(accountRepository.findAccountByAccountId(account.getAccountId())).thenReturn(account);
        when(accountRepository.save(account)).thenThrow(new TransactionSystemException("Error"));

        accountService.updateAccount(account.getAccountId(), account);
    }

    @Test
    public void testShouldReturnMethodFindAccountByIsVisibleWasCalledThreeTimes() {
        when(accountRepository.findAccountByIsVisible(account.isVisible())).thenReturn(new ArrayList<>());

        accountService.getAllAccounts();
        accountService.getAllAccounts();
        accountService.getAllAccounts();

        verify(accountRepository, times(3)).findAccountByIsVisible(account.isVisible());
    }

    @Test
    public void testOfMethodGetAllAccountsShouldReturnListSizeOf2() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        accounts.add(account2);

        when(accountRepository.findAccountByIsVisible(true)).thenReturn(accounts);

        List<Account> accountsFound = accountService.getAllAccounts();

        assertThat(accountsFound.size(), is(2));
    }

    @Test
    public void testOfMethodFindAccountsByOwnerNameShouldReturnListSizeOf1() {
        List<Account> accountsByOwnerName = new ArrayList<>();
        accountsByOwnerName.add(account2);

        when(accountRepository.findAccountsByOwnerName("Owner2")).thenReturn(accountsByOwnerName);

        List<Account> accountsFound = accountService.findAccountByOwnerName("Owner2");

        assertThat(accountsFound.size(), is(1));
    }

    @Test
    public void testOfMethodGetAllExternalAccountsShouldReturnListSizeOf5() {
        List<ExternalAccountDto> listExternalsAccounts = accountService.getAllExternalsAccounts();

        assertThat(listExternalsAccounts.size(), is(5));
    }
}