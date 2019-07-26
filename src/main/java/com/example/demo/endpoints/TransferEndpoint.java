package com.example.demo.endpoints;

import com.example.demo.entities.Transfer;
import com.example.demo.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/")
public class TransferEndpoint {

    private TransferService transferService;

    @Autowired
    public TransferEndpoint(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("transfers")
    public ResponseEntity<List<Transfer>> getTransfers() {
        return new ResponseEntity<>(transferService.getAllTransfers(), HttpStatus.OK);
    }

    @PutMapping("accounts/transfer/{accountNumberFrom}/{accountNumberTo}/{money}")
    public ResponseEntity<?> makeTransfer(@PathVariable String accountNumberFrom, @PathVariable String accountNumberTo, @PathVariable Double money) {
        return new ResponseEntity<>(transferService.makeTransfer(accountNumberFrom, accountNumberTo, money), HttpStatus.OK);
    }

    @GetMapping("transfers/findByAccountId/{accountId}")
    public ResponseEntity<?> getTransfersByAccountId(@PathVariable long accountId) {
        return new ResponseEntity<>(transferService.getTransfersByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("transfersOut/findByAccountId/{accountId}")
    public ResponseEntity<?> getTransfersOutByAccountId(@PathVariable long accountId) {
        return new ResponseEntity<>(transferService.getTransfersOutByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("transfersIn/findByAccountId/{accountId}")
    public ResponseEntity<?> getTransfersInByAccountId(@PathVariable long accountId) {
        return new ResponseEntity<>(transferService.getTransfersInByAccountId(accountId), HttpStatus.OK);
    }

    @PutMapping("transfers/cancel/{transferId}")
    public ResponseEntity<?> cancelTransfer(@PathVariable long transferId) {
        return new ResponseEntity<>(transferService.cancelTransfer(transferId), HttpStatus.OK);
    }
}
