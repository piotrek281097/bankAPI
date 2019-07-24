package com.example.demo.services;

import com.example.demo.entities.Account;
import com.example.demo.entities.ExternalTransfer;

public interface ExternalTransferService {

    Account makeExternalTransfer(ExternalTransfer externalTransfer);
}
