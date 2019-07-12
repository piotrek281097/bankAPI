package com.example.demo.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
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
    private Double money;
    private String currency;
    private String ownerName;


    Account(String accountNumber, Double money, String currency, String ownerName)
    {
        this.accountNumber = accountNumber;
        this.money = money;
        this.currency = currency;
        this.ownerName = ownerName;
    }

}
