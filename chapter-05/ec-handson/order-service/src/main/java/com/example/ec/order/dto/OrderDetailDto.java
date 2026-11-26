package com.example.ec.order.dto;

import java.math.BigDecimal;

/** 受注明細（T_ORDER_DETAILの1行）。 */
public record OrderDetailDto(
        int lineNo,
        String productCode,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal amount) {
}
