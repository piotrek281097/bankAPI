package com.example.demo.service;

import com.example.demo.DTOs.FineDto;
import com.example.demo.domain.Fine;

public interface FineService {
    Fine addFine(FineDto fine);

    Fine updateFine(FineDto fine);
}
