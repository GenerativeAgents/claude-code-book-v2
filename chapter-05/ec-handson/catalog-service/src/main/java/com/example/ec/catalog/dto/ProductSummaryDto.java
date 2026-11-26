package com.example.ec.catalog.dto;

import java.math.BigDecimal;

/**
 * 商品検索（CAT-002）の結果の1件。
 *
 * @param availableQuantity 引当可能数
 */
public record ProductSummaryDto(
        String productCode,
        String productName,
        String categoryCode,
        BigDecimal unitPrice,
        int availableQuantity) {
}
