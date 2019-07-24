package com.example.demo.repositories;

import com.example.demo.entities.ExternalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalTransferRepository extends JpaRepository<ExternalTransfer, Long> {

}
