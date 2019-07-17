package com.example.demo.entities;

import com.example.demo.enums.TransferStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transferId;

    private long firstAccountId;
    private long secondAccountId;
    @Size(min = 26, max = 26)
    private String firstAccountNumber;
    @Size(min = 26, max = 26)
    private String secondAccountNumber;
    private double moneyBeforeConverting;
    private double money;
    private String currency;
    private LocalDateTime dataOpenTransfer;
    private LocalDateTime dataFinishTransfer;
    private TransferStatus transferStatus;


    public Transfer(long firstAccountId, long secondAccountId, String firstAccountNumber, String secondAccountNumber, Double moneyBeforeConverting, Double money, String currency, LocalDateTime dataOpenTransfer, LocalDateTime dataFinishTransfer, TransferStatus transferStatus) {
        this.firstAccountId = firstAccountId;
        this.secondAccountId = secondAccountId;
        this.firstAccountNumber = firstAccountNumber;
        this.secondAccountNumber = secondAccountNumber;
        this.moneyBeforeConverting = moneyBeforeConverting;
        this.money = money;
        this.currency = currency;
        this.dataOpenTransfer = dataOpenTransfer;
        this.dataFinishTransfer = dataFinishTransfer;
        this.transferStatus = transferStatus;
    }
}
