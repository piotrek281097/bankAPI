package com.example.demo.domain;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
@Entity
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String telephone;

    @NotNull
    private String pesel;

    @OneToMany(mappedBy = "reader")
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "reader")
    private Set<Fine> fines;

    @Version
    private int optLock;
}

