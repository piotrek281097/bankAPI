package com.example.demo.domain;

import com.example.demo.enums.BookStatus;
import com.example.demo.enums.BookKind;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String title;

    private String author;

    @NotNull
    private String identifier;

    @Enumerated(EnumType.STRING)
    private BookKind bookKind;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private Set<Reservation> reservations;

    @Version
    private int optLock;
}
