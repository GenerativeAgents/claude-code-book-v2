package com.example.ec.catalog.dao;

import com.example.ec.catalog.dto.ProductDetailDto;
import com.example.ec.common.sql.SqlLoader;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** 商品（M_PRODUCT）のDao。 */
@Repository
public class ProductDao {

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public ProductDao(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /** 削除されていない商品を在庫とあわせて1件取得する。販売状態は問わない。 */
    public Optional<ProductDetailDto> findDetail(String productCode) {
        return jdbcClient.sql(sqlLoader.load("SEL_PRODUCT_DETAIL"))
                .param(productCode)
                .query((rs, rowNum) -> new ProductDetailDto(
                        rs.getString("PRODUCT_CODE"),
                        rs.getString("PRODUCT_NAME"),
                        rs.getString("CATEGORY_CODE"),
                        rs.getBigDecimal("UNIT_PRICE"),
                        rs.getString("SALES_STATUS"),
                        rs.getInt("AVAILABLE_QUANTITY")))
                .optional();
    }
}
