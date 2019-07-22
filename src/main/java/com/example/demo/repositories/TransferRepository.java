package com.example.demo.repositories;

import com.example.demo.entities.Transfer;
import com.example.demo.enums.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findBySendingAccountAccountId(long accountId);
    List<Transfer> findByTargetAccountAccountId(long accountId);
    Transfer findByTransferId(long transferId);
    List<Transfer> findByTransferStatus(String transferStatus);
}