package com.example.ec.order.client;

/** 在庫引当を依頼する明細。 */
public record ReserveLine(int lineNo, String productCode, int quantity) {
}
