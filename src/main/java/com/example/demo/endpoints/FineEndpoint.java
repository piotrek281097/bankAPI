package com.example.demo.endpoints;

import com.example.demo.DTOs.FineDto;
import com.example.demo.domain.Fine;
import com.example.demo.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/")
public class FineEndpoint {

    private FineService fineService;

    @Autowired
    public FineEndpoint(FineService fineService) {
        this.fineService = fineService;
    }

    @PostMapping("fines/add")
    public ResponseEntity<Fine> addReservation(@RequestBody FineDto fine) {
        return new ResponseEntity<Fine>(fineService.addFine(fine), HttpStatus.OK);
    }

    @PostMapping("fines/update")
    public ResponseEntity<Fine> updateReader(@RequestBody FineDto fine) {
        return new ResponseEntity<Fine>(fineService.updateFine(fine), HttpStatus.OK);
    }

}
