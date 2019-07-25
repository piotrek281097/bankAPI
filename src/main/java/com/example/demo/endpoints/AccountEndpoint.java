package com.example.demo.endpoints;

import com.example.demo.DTOs.ExternalAccountDto;
import com.example.demo.entities.Account;
import com.example.demo.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/")
public class AccountEndpoint {

    private AccountService accountService;

    @Autowired
    public AccountEndpoint(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
    }

    @GetMapping("accounts-external")
    public ResponseEntity<List<ExternalAccountDto>> getAllExternalAccounts() {
        return new ResponseEntity<>(accountService.getAllExternalsAccounts(), HttpStatus.OK);
    }

    @PostMapping("accounts/add")
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        accountService.addAccount(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PutMapping("accounts/update/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable long accountId, @RequestBody Account account) {
        accountService.updateAccount(accountId, account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("accounts/delete/{accountId}")
    public ResponseEntity<?> deleteAccountByAccountId(@PathVariable long accountId) {
        accountService.deleteAccountById(accountId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("accounts/findAccountByAccountId/{accountId}")
    public ResponseEntity<Account> findAccountByAccountNumber(@PathVariable long accountId) {
        return new ResponseEntity<>(accountService.findAccountByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("accounts/findByOwnerName/{ownerName}")
    public ResponseEntity<List<Account>> findAccountByOwnerName(@PathVariable String ownerName) {
        return new ResponseEntity<>(accountService.findAccountByOwnerName(ownerName), HttpStatus.OK);
    }

}
