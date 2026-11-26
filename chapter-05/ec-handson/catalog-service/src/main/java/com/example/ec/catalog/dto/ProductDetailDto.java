package com.example.ec.catalog.dto;

import java.math.BigDecimal;

/**
 * 商品照会（CAT-001）の結果。
 *
 * @param availableQuantity 引当可能数
 */
public record ProductDetailDto(
        String productCode,
        String productName,
        String categoryCode,
        BigDecimal unitPrice,
        String salesStatus,
        int availableQuantity) {
}
