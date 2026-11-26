package com.example.ec.catalog.dao;

import com.example.ec.catalog.dto.StockLowDto;
import com.example.ec.common.sql.SqlLoader;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** 在庫レポートのDao。 */
@Repository
public class StockReportDao {

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public StockReportDao(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /** 引当可能数がしきい値未満の販売中商品を、商品コード順に取得する。 */
    public List<StockLowDto> findLowStock(int threshold) {
        return jdbcClient.sql(sqlLoader.load("SEL_STOCK_LOW"))
                .param(threshold)
                .query((rs, rowNum) -> new StockLowDto(
                        rs.getString("PRODUCT_CODE"),
                        rs.getString("PRODUCT_NAME"),
                        rs.getInt("AVAILABLE_QUANTITY")))
                .list();
    }
}
