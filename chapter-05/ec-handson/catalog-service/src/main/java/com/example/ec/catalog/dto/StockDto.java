package com.example.ec.catalog.dto;

/** 在庫（M_STOCK）の1件。 */
public record StockDto(String productCode, int stockQuantity, int reservedQuantity) {
}
