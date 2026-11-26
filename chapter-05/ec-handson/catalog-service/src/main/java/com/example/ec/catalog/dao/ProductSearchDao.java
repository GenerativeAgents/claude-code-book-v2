package com.example.ec.catalog.dao;

import com.example.ec.catalog.dto.ProductSearchCondition;
import com.example.ec.catalog.dto.ProductSummaryDto;
import com.example.ec.common.sql.SqlLoader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** 商品検索（CAT-002）のDao。SEL_PRODUCT_SEARCH_BASEの後ろに、指定された検索条件を連結する。 */
@Repository
public class ProductSearchDao {

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public ProductSearchDao(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /** 販売中の商品を条件で絞り込み、商品コード順に返す。 */
    public List<ProductSummaryDto> search(ProductSearchCondition condition) {
        StringBuilder sql = new StringBuilder(sqlLoader.load("SEL_PRODUCT_SEARCH_BASE"));
        List<Object> params = new ArrayList<>();
        if (condition.keyword() != null) {
            sql.append(" AND P.PRODUCT_NAME LIKE ?");
            params.add("%" + condition.keyword() + "%");
        }
        if (condition.categoryCode() != null) {
            sql.append(" AND P.CATEGORY_CODE = ?");
            params.add(condition.categoryCode());
        }
        if (condition.inStockOnly()) {
            sql.append(" AND S.STOCK_QUANTITY - S.RESERVED_QUANTITY > 0");
        }
        sql.append(" ORDER BY P.PRODUCT_CODE");

        return jdbcClient.sql(sql.toString())
                .params(params)
                .query((rs, rowNum) -> new ProductSummaryDto(
                        rs.getString("PRODUCT_CODE"),
                        rs.getString("PRODUCT_NAME"),
                        rs.getString("CATEGORY_CODE"),
                        rs.getBigDecimal("UNIT_PRICE"),
                        rs.getInt("AVAILABLE_QUANTITY")))
                .list();
    }
}
