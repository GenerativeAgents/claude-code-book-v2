package com.example.ec.order.dao;

import com.example.ec.common.sql.SqlLoader;
import com.example.ec.order.dto.OrderDetailDto;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** 受注明細（T_ORDER_DETAIL）のDao。 */
@Repository
public class OrderDetailDao {

    private static final RowMapper<OrderDetailDto> ORDER_DETAIL_ROW_MAPPER = (rs, rowNum) -> new OrderDetailDto(
            rs.getInt("LINE_NO"),
            rs.getString("PRODUCT_CODE"),
            rs.getInt("QUANTITY"),
            rs.getBigDecimal("UNIT_PRICE"),
            rs.getBigDecimal("AMOUNT"));

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public OrderDetailDao(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /** 受注番号の明細を行番号順に取得する。 */
    public List<OrderDetailDto> findByOrderNo(String orderNo) {
        return jdbcClient.sql(sqlLoader.load("SEL_ORDER_DETAIL"))
                .param(orderNo)
                .query(ORDER_DETAIL_ROW_MAPPER)
                .list();
    }
}
