package com.example.demo.endpoints;

import com.example.demo.entities.Account;
import com.example.demo.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PostMapping("accounts/add")
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        accountService.addAccount(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }


    @PutMapping("accounts/{accountNumber}")
    public ResponseEntity<Account> updateAccount(@PathVariable String accountNumber, @RequestBody Account account) {

        accountService.updateAccount(accountNumber, account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("accounts/{accountNumber}")
    public ResponseEntity<?> deleteAccountByNumber(@PathVariable String accountNumber) {

        accountService.deleteAccountByNumber(accountNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("accounts/{accountNumber}")
    public ResponseEntity<Account> findAccountByAccountNumber(@PathVariable String accountNumber, @RequestBody Account account) {

        return new ResponseEntity<>(accountService.findAccountByAccountNumber(accountNumber), HttpStatus.OK);
    }

}
