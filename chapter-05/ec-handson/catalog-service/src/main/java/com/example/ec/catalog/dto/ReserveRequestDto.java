package com.example.ec.catalog.dto;

import java.util.List;

/** 在庫引当（CAT-020）のリクエスト。 */
public record ReserveRequestDto(String orderNo, List<ReserveLineDto> lines) {
}
