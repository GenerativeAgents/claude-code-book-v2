package com.example.ec.catalog.dto;

/**
 * 商品検索（CAT-002）の条件。
 *
 * @param keyword      商品名の部分一致。指定なしはnull
 * @param categoryCode カテゴリコード。指定なしはnull
 * @param inStockOnly  引当可能な在庫がある商品に限るか
 */
public record ProductSearchCondition(String keyword, String categoryCode, boolean inStockOnly) {
}
