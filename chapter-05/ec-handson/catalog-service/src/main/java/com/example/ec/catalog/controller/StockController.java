package com.example.ec.catalog.controller;

import com.example.ec.catalog.dto.ReleaseRequestDto;
import com.example.ec.catalog.dto.ReserveRequestDto;
import com.example.ec.catalog.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 在庫引当（CAT-020）と在庫引当解除（CAT-021）のAPI。 */
@RestController
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /** POST /stocks/reserve */
    @PostMapping("/stocks/reserve")
    public ResponseEntity<Void> reserve(@RequestBody ReserveRequestDto request) {
        stockService.reserve(request);
        return ResponseEntity.noContent().build();
    }

    /** POST /stocks/release */
    @PostMapping("/stocks/release")
    public ResponseEntity<Void> release(@RequestBody ReleaseRequestDto request) {
        stockService.release(request);
        return ResponseEntity.noContent().build();
    }
}
