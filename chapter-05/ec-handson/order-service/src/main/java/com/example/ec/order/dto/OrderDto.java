package com.example.ec.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 受注（T_ORDERの1件と顧客名、明細）。
 *
 * @param orderStatus 受注ステータス（10:受付 20:引当済 90:取消）
 */
public record OrderDto(
        String orderNo,
        LocalDate orderDate,
        String customerCode,
        String customerName,
        String deliveryPostalCode,
        String remarks,
        BigDecimal totalAmount,
        String orderStatus,
        List<OrderDetailDto> details) {

    /** 明細を差し替えた受注を返す。 */
    public OrderDto withDetails(List<OrderDetailDto> newDetails) {
        return new OrderDto(orderNo, orderDate, customerCode, customerName, deliveryPostalCode,
                remarks, totalAmount, orderStatus, newDetails);
    }
}
