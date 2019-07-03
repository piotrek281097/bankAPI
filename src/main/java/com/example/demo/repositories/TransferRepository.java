package com.example.demo.repositories;

import com.example.demo.entities.Account;
import com.example.demo.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByFirstAccountNumber(String accountNumber);
    List<Transfer> findBySecondAccountNumber(String accountNumber);
}