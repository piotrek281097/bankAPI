package com.example.demo.entities;

import com.example.demo.TransferStatus;
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
    private long accountId;

    @Size(min = 26, max = 26)
    private String firstAccountNumber;
    @Size(min = 26, max = 26)
    private String secondAccountNumber;
    private double money;
    private String currency;
    private LocalDateTime dataOpenTransfer;
    private LocalDateTime dataFinishTransfer;
    private TransferStatus transferStatus;


    public Transfer(String firstAccountNumber, String secondAccountNumber, Double money, String currency, LocalDateTime dataOpenTransfer, LocalDateTime dataFinishTransfer, TransferStatus transferStatus) {
        this.firstAccountNumber = firstAccountNumber;
        this.secondAccountNumber = secondAccountNumber;
        this.money = money;
        this.currency = currency;
        this.dataOpenTransfer = dataOpenTransfer;
        this.dataFinishTransfer = dataFinishTransfer;
        this.transferStatus = transferStatus;
    }
}
