package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "externalTransfers")

public class ExternalTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long externalTransferId;

    private BigDecimal amount;
    private String bankName;
    private String currency;
    private String externalAccount;
    private String toAccount;

    public ExternalTransfer(String externalAccount, String toAccount, BigDecimal amount, String currency, String bankName) {
        this.externalAccount = externalAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.currency = currency;
        this.bankName = bankName;
    }
}
