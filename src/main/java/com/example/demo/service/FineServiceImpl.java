package com.example.demo.service;

import com.example.demo.DTOs.FineDto;
import com.example.demo.domain.Fine;
import com.example.demo.enums.FineStatus;
import com.example.demo.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FineServiceImpl implements FineService {

    private FineRepository fineRepository;

    @Autowired
    public FineServiceImpl(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    @Override
    public Fine addFine(FineDto fineDto) {
        Fine fine = fillFine(fineDto, false);

        return fineRepository.save(fine);
    }

    @Override
    public Fine updateFine(FineDto fineDto) {
        Fine fine = fillFine(fineDto, true);

        return fineRepository.save(fine);
    }

    private Fine fillFine(FineDto fineDto, boolean update) {
        Fine fine = new Fine();
        if (update) {
            fine.setId(fineDto.getId());
            fine.setFineStatus(FineStatus.PAID);
        } else {
            fine.setFineStatus(FineStatus.UNPAID);
        }
        fine.setText(fineDto.getText());
        fine.setMoney(fineDto.getMoney());
        fine.setReader(fineDto.getReader());
        return fine;
    }
}
