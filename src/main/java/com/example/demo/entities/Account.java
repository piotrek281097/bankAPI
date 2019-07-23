package com.example.demo.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@NoArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @Size(min = 26, max = 26)
    @Column(unique = true)
    private String accountNumber;
    @NotNull
    @NumberFormat
    private Double money;
    @NotNull
    private String currency;
    @NotNull
    private String ownerName;
    private boolean isVisible = true;

    Account(String accountNumber, Double money, String currency, String ownerName)
    {
        this.accountNumber = accountNumber;
        this.money = money;
        this.currency = currency;
        this.ownerName = ownerName;
    }

}
