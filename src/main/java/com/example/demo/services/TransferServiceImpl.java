package com.example.demo.services;

import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.enums.TransferStatus;
import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import com.example.demo.exceptions.*;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
public class TransferServiceImpl implements TransferService {

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    public TransferServiceImpl(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Override
    public List<Account> makeTransfer(String accountNumberFrom, String accountNumberTo, Double valueOfTransfer, String email) {
        Double newMoneyAmountToFirstAccount, moneyTransferAmountTo;
        List<Account> updatedAccountsList = new ArrayList<>();

        if (!accountNumberFrom.equals(accountNumberTo)) {
            valueOfTransfer = roundValue(valueOfTransfer);
            Account firstAccount = accountRepository.findAccountByAccountNumber(accountNumberFrom);
            Account secondAccount = accountRepository.findAccountByAccountNumber(accountNumberTo);

            checkingIfAccountsExist(firstAccount, secondAccount);

            if (firstAccount.getCurrency().equals(secondAccount.getCurrency())) {
                newMoneyAmountToFirstAccount = firstAccount.getMoney() - valueOfTransfer;
                moneyTransferAmountTo = valueOfTransfer;
            } else {
                moneyTransferAmountTo = convertCurrencies(firstAccount.getCurrency(), secondAccount.getCurrency(), valueOfTransfer);
                newMoneyAmountToFirstAccount = firstAccount.getMoney() - valueOfTransfer;
            }

            checkingIfThereIsEnoughMoneyToMakeTransfer(newMoneyAmountToFirstAccount);
            firstAccount.setMoney(newMoneyAmountToFirstAccount);

            updatedAccountsList.add(firstAccount);
            updatedAccountsList.add(secondAccount);

            accountRepository.save(firstAccount);

            addTransfer(new Transfer(firstAccount, secondAccount,
                    valueOfTransfer, moneyTransferAmountTo, secondAccount.getCurrency(), LocalDateTime.now(), null, TransferStatus.OPENED.getValue()));

            sendConfirmingTransferEmail(email, accountNumberFrom, accountNumberTo, valueOfTransfer);

            return updatedAccountsList;
        } else return Collections.emptyList();
    }

    @Override
    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    private void addTransfer(Transfer transfer) {
        transferRepository.save(transfer);
    }

    @Override
    public List<Transfer> getTransfersByAccountId(long accountId) {
        List<Transfer> transfersFrom = transferRepository.findBySendingAccountAccountId(accountId);
        List<Transfer> transfersTo = transferRepository.findByTargetAccountAccountId(accountId);

        List<Transfer> transfers = new ArrayList<>();
        transfers.addAll(transfersFrom);
        transfers.addAll(transfersTo);

        return transfers;
    }

    @Override
    public List<Transfer> getTransfersOutByAccountId(long accountId) {
        return transferRepository.findBySendingAccountAccountId(accountId);
    }

    @Override
    public List<Transfer> getTransfersInByAccountId(long accountId) {
        return transferRepository.findByTargetAccountAccountId(accountId);
    }

    private Double convertCurrencies(String currency1, String currency2, Double valueOfTransfer) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangeratesapi.io/latest?base=" + currency1;
            ResponseEntity<CurrencyDto> response = restTemplate.getForEntity(url, CurrencyDto.class);
            CurrencyDto currencyDto = response.getBody();

            return roundValue(valueOfTransfer * currencyDto.getRates().get(currency2));
        } catch (NullPointerException ex) {
            throw new CurrencyIsNotAvailableException("Blad w konwersji walut");
        } catch (ResourceAccessException ex) {
            throw new ConnectionException("Blad w polaczeniu z API");
        }
    }

    public void finishTransfers() {
        List<Transfer> transfers = transferRepository.findByTransferStatus(TransferStatus.OPENED.getValue());

        for (Transfer transfer: transfers) {
            Account secondAccount = accountRepository.findAccountByAccountNumber(transfer.getTargetAccount().getAccountNumber());
            if (secondAccount != null && secondAccount.isVisible()) {
                secondAccount.setMoney(roundValue(secondAccount.getMoney() + transfer.getMoney()));
                accountRepository.save(secondAccount);

                transfer.setTransferStatus(TransferStatus.FINISHED.getValue());
                transfer.setDataFinishTransfer(LocalDateTime.now());
                transferRepository.save(transfer);
            } else {
                cancelTransfer(transfer.getTransferId());
                throw new AccountAlreadyDeletedException("Konto zostało usunięte! Nie można dokonać zaplanowanych czynności");
            }
        }
    }

    private static double roundValue(Double value) {
        String newValue = new DecimalFormat("##.##").format(value);
        newValue = newValue.replace(",", ".");

        return Double.parseDouble(newValue);
    }

    @Override
    public Transfer cancelTransfer(long transferId) {
        Transfer canceledTransfer = transferRepository.findByTransferId(transferId);
        if (canceledTransfer.getTransferStatus().equals(TransferStatus.OPENED.getValue())) {
            canceledTransfer.setTransferStatus(TransferStatus.CANCELED.getValue());
            transferRepository.save(canceledTransfer);

            Account updatedAccountFrom = accountRepository.findAccountByAccountNumber(canceledTransfer.getSendingAccount().getAccountNumber());
            updatedAccountFrom.setMoney(updatedAccountFrom.getMoney() + canceledTransfer.getMoneyBeforeConverting());
            accountRepository.save(updatedAccountFrom);
        } else if (canceledTransfer.getTransferStatus().equals(TransferStatus.FINISHED.getValue())) {
            throw new TransferCantBeCanceledException("Transfer już zakończony, nie można anulować");
        }

        return canceledTransfer;
    }

    private void checkingIfAccountsExist(Account sendingAccount, Account targetAccount) {
        if (sendingAccount == null) {
            throw new AccountDoesNotExistException("Rachunek z ktorego mial byc przelew nie istnieje 1");
        } else if (targetAccount == null) {
            throw new AccountDoesNotExistException("Rachunek na ktory mial byc przelew nie istnieje 2");
        }
    }

    private void checkingIfThereIsEnoughMoneyToMakeTransfer(Double newMoney) {
        if (newMoney < 0) {
            throw new NotEnoughMoneyToMakeTransferException("Za malo pieniedzy");
        }
    }

    private void sendConfirmingTransferEmail(String email, String sendingAccountNumber, String targetAccountNumber, Double money) {
/*
        System.out.println("--------------------------" + "piotr.plecinski1997@wp.pl" + " " + sendingAccountNumber + " " + targetAccountNumber + " " + money);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("piotr.plecinski1997@wp.pl");

        msg.setSubject("Potwierdzenie przelewu");
        msg.setText("Przelew zostal wykonany." +
                "\nNumer nadawcy: " + sendingAccountNumber +
                 "\nNumer odbiorcy: " + targetAccountNumber +
                "\nPienidze: " + money);

        javaMailSender.send(msg);
  */

        System.out.println("MAIL_________________________________________");

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", true);
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("piotrbankapi", "Piotrek2810$");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("piotrbankapi@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse("piotrbankapi2@gmail.com"));
            message.setSubject("Potwierdzenie przelewu");

            String msg = "Przelew został zrobiony";

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        }
        catch (Exception ex) {
            throw new ConnectionException("Bład z mailem");
        }

    }


}
