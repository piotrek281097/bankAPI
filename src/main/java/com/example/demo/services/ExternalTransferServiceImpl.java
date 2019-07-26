package com.example.demo.services;

import com.example.demo.DTOs.ExternalTransferDto;
import com.example.demo.entities.Account;
import com.example.demo.entities.ExternalTransfer;
import com.example.demo.exceptions.AccountDoesNotExistException;
import com.example.demo.exceptions.NotEnoughMoneyToMakeTransferException;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.ExternalTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.text.DecimalFormat;


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
                externalTransfer.setAmount(BigDecimal.valueOf(roundValue(Double.parseDouble(externalTransfer.getAmount().toString()))));
                Account myAccount = accountRepository.findAccountByAccountNumber(externalTransfer.getExternalAccount());

                checkingIfAccountExists(myAccount);

                externalTransfer.setCurrency(myAccount.getCurrency());

                checkingIfThereIsEnoughMoneyToMakeTransfer(myAccount, externalTransfer.getAmount().doubleValue());
                myAccount.setMoney(myAccount.getMoney() - externalTransfer.getAmount().doubleValue());

                accountRepository.save(myAccount);

                sendExternalTransfer(externalTransfer, email);

            return myAccount;
        } else return null;
    }

    private void addExternalTransfer(ExternalTransfer externalTransfer) {
        externalTransferRepository.save(externalTransfer);
    }

    private void checkingIfAccountExists(Account account) {
        if(account == null) {
            throw new AccountDoesNotExistException("Rachunek nie istnieje!");
        }
    }

    private void checkingIfThereIsEnoughMoneyToMakeTransfer(Account myAccount, Double moneyTransfer) {
        if ((myAccount.getMoney() - moneyTransfer) < 0) {
            throw new NotEnoughMoneyToMakeTransferException("Za malo pieniedzy");
        }
    }

    private static double roundValue(Double value) {
        String newValue = new DecimalFormat("##.##").format(value);
        newValue = newValue.replace(",", ".");

        return Double.parseDouble(newValue);
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
