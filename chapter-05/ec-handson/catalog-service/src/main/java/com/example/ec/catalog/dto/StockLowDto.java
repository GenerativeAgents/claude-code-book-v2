package com.example.ec.catalog.dto;

/**
 * 在庫僅少レポートの1行。
 *
 * @param availableQuantity 引当可能数
 */
public record StockLowDto(String productCode, String productName, int availableQuantity) {
}
