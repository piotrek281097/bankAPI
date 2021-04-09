package com.example.demo.domain;

import com.example.demo.enums.FineStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;

    private double money;

    @Enumerated(EnumType.STRING)
    private FineStatus fineStatus;

    @ManyToOne
    @JsonIgnore
    private Reader reader;

    @Version
    private int optLock;
}

