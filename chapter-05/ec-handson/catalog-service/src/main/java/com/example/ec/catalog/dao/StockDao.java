package com.example.ec.catalog.dao;

import com.example.ec.catalog.dto.StockDto;
import com.example.ec.common.sql.SqlLoader;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** 在庫（M_STOCK）のDao。 */
@Repository
public class StockDao {

    /** 引当数を加算する。 */
    public static final String ACTION_RESERVE = "RESERVE";
    /** 引当数を減算する。 */
    public static final String ACTION_RELEASE = "RELEASE";

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public StockDao(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /** 在庫の行をロックして取得する。 */
    public Optional<StockDto> findForUpdate(String productCode) {
        return jdbcClient.sql(sqlLoader.load("SEL_STOCK_FOR_UPDATE"))
                .param(productCode)
                .query((rs, rowNum) -> new StockDto(
                        rs.getString("PRODUCT_CODE"),
                        rs.getInt("STOCK_QUANTITY"),
                        rs.getInt("RESERVED_QUANTITY")))
                .optional();
    }

    /**
     * 引当数を更新する。actionに応じたSQLを読む。
     *
     * @param action {@link #ACTION_RESERVE}または{@link #ACTION_RELEASE}
     */
    public void updateReserved(String action, String productCode, int quantity) {
        jdbcClient.sql(sqlLoader.load("UPD_STOCK_" + action))
                .param(quantity)
                .param(productCode)
                .update();
    }
}
