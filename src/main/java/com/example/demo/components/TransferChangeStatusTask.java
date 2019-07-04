package com.example.demo.components;

import com.example.demo.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransferChangeStatusTask {

    private TransferService transferService;

    @Autowired
    public TransferChangeStatusTask(TransferService transferService) {
        this.transferService = transferService;
    }

    @Scheduled(fixedRate = 30000)
    public void reportCurrentTime() {
        transferService.finishTransfers();
    }
}
