package com.example.ec.catalog.dao;

import com.example.ec.catalog.dto.ReserveLineDto;
import com.example.ec.common.sql.SqlLoader;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** 引当（T_RESERVATION）のDao。 */
@Repository
public class ReservationDao {

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public ReservationDao(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /** 引当中の明細を1行登録する。 */
    public void insert(String orderNo, ReserveLineDto line) {
        jdbcClient.sql(sqlLoader.load("INS_RESERVATION"))
                .param(orderNo)
                .param(line.lineNo())
                .param(line.productCode())
                .param(line.quantity())
                .update();
    }

    /** 受注番号の引当中の明細をロックして、明細番号順に取得する。 */
    public List<ReserveLineDto> findActiveByOrderNoForUpdate(String orderNo) {
        return jdbcClient.sql(sqlLoader.load("SEL_RESERVATION_BY_ORDER_FOR_UPDATE"))
                .param(orderNo)
                .query((rs, rowNum) -> new ReserveLineDto(
                        rs.getInt("LINE_NO"),
                        rs.getString("PRODUCT_CODE"),
                        rs.getInt("QUANTITY")))
                .list();
    }

    /** 受注番号の引当中の明細を解除済にする。 */
    public void markReleased(String orderNo) {
        jdbcClient.sql(sqlLoader.load("UPD_RESERVATION_RELEASED"))
                .param(orderNo)
                .update();
    }
}
