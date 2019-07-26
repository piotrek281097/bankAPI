package com.example.demo.DTOs;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
public class ExternalTransferDto {

    private BigDecimal amount;

    private String bankName;

    private String currency;

    private String externalAccount;

    private String toAccount;
}
