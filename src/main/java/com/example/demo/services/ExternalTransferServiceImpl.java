package com.example.demo.services;

import com.example.demo.DTOs.ExternalTransferDto;
import com.example.demo.Utils.CheckingMethodsObject;
import com.example.demo.Utils.MathematicalMethodsObject;
import com.example.demo.entities.Account;
import com.example.demo.entities.ExternalTransfer;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.ExternalTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;


@Service
public class ExternalTransferServiceImpl implements ExternalTransferService {

    private AccountRepository accountRepository;
    private ExternalTransferRepository externalTransferRepository;
    private EmailService emailService;

    @Autowired
    public ExternalTransferServiceImpl(AccountRepository accountRepository, ExternalTransferRepository externalTransferRepository, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.externalTransferRepository = externalTransferRepository;
        this.emailService = emailService;
    }

    @Override
    public Account makeExternalTransfer(ExternalTransfer externalTransfer, String email) {

            if(!externalTransfer.getExternalAccount().equals(externalTransfer.getToAccount())) {
                externalTransfer.setAmount(BigDecimal.valueOf(MathematicalMethodsObject.roundValue(Double.parseDouble(externalTransfer.getAmount().toString()))));
                Account myAccount = accountRepository.findAccountByAccountNumber(externalTransfer.getExternalAccount());

                CheckingMethodsObject.checkingIfAccountExists(myAccount);

                externalTransfer.setCurrency(myAccount.getCurrency());

                CheckingMethodsObject.checkingIfThereIsEnoughMoneyToMakeTransfer(myAccount, externalTransfer.getAmount().doubleValue());

                myAccount.setMoney(myAccount.getMoney() - externalTransfer.getAmount().doubleValue());

                accountRepository.save(myAccount);

                sendExternalTransfer(externalTransfer, email);

            return myAccount;
        } else return null;
    }

    private void addExternalTransfer(ExternalTransfer externalTransfer) {
        externalTransferRepository.save(externalTransfer);
    }

    private void sendExternalTransfer(ExternalTransfer externalTransfer, String email) {

        RestTemplate restTemplate = new RestTemplate();
        ExternalTransferDto dto = ExternalTransferDto.builder()
                                                     .amount(externalTransfer.getAmount())
                                                     .bankName(externalTransfer.getBankName())
                                                     .currency(externalTransfer.getCurrency())
                                                     .externalAccount(externalTransfer.getExternalAccount())
                                                     .toAccount(externalTransfer.getToAccount())
                                                     .build();

        ResponseEntity<Object> objectResponseEntity = restTemplate.postForEntity("https://comarch.herokuapp.com/transfer/external-transfer", dto, null);

        if(objectResponseEntity.getStatusCode() == HttpStatus.OK) {
            emailService.sendConfirmingTransferEmail(email, externalTransfer.getExternalAccount(),
                    externalTransfer.getToAccount(), externalTransfer.getAmount().doubleValue());

            addExternalTransfer(externalTransfer);
        }
    }
}
