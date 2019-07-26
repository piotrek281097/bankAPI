package com.example.demo.endpoints;

import com.example.demo.entities.ExternalTransfer;
import com.example.demo.services.ExternalTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api/")
public class ExternalTransferEndpoint {

    private ExternalTransferService externalTransferService;

    @Autowired
    public ExternalTransferEndpoint(ExternalTransferService externalTransferService) {
        this.externalTransferService = externalTransferService;
    }

    @PostMapping("accounts/transfer-external/{email}")
    public ResponseEntity<?> makeExternalTransfer(@RequestBody ExternalTransfer externalTransfer, @PathVariable String email) {
        return new ResponseEntity<>(externalTransferService.makeExternalTransfer(externalTransfer, email), HttpStatus.OK);
    }

}
