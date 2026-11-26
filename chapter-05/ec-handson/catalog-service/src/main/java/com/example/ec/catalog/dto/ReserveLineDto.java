package com.example.ec.catalog.dto;

/** 引当の明細。在庫引当のリクエストと、引当（T_RESERVATION）の読み取りで使う。 */
public record ReserveLineDto(int lineNo, String productCode, int quantity) {
}
