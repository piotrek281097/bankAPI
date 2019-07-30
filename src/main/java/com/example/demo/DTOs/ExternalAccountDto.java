package com.example.demo.DTOs;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalAccountDto {
    private long id;
    private String number;
    private Double money;
    private String currency;
    private String owner;
    private boolean deleted;
}
