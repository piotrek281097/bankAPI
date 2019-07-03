package com.example.demo.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

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



    public Transfer(String firstAccountNumber, String secondAccountNumber, Double money) {
        this.firstAccountNumber = firstAccountNumber;
        this.secondAccountNumber = secondAccountNumber;
        this.money = money;
    }




}
