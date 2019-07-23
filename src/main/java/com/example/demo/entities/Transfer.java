package com.example.demo.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transferId;
    @OneToOne
    private Account sendingAccount;
    @OneToOne
    private Account targetAccount;
    private double moneyBeforeConverting;
    private double money;
    private String currency;
    private LocalDateTime dataOpenTransfer;
    private LocalDateTime dataFinishTransfer;
    private String transferStatus;

    public Transfer(Account sendingAccount, Account targetAccount, Double moneyBeforeConverting, Double money, String currency, LocalDateTime dataOpenTransfer, LocalDateTime dataFinishTransfer, String transferStatus) {
        this.sendingAccount = sendingAccount;
        this.targetAccount = targetAccount;
        this.moneyBeforeConverting = moneyBeforeConverting;
        this.money = money;
        this.currency = currency;
        this.dataOpenTransfer = dataOpenTransfer;
        this.dataFinishTransfer = dataFinishTransfer;
        this.transferStatus = transferStatus;
    }
}
