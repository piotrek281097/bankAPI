package com.example.demo.services;

public interface EmailService {

    void sendConfirmingTransferEmail(String email, String sendingAccountNumber, String targetAccountNumber, Double money);
}
