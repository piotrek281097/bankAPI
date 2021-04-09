package com.example.demo.DTOs;

import com.example.demo.domain.Reader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageableReaderResponse {

    private List<Reader> readers;

    private int totalPages;

    private int pageNumber;

    private int pageSize;

}
