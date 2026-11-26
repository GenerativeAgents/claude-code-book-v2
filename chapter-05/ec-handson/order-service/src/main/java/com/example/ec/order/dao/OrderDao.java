package com.example.ec.order.dao;

import com.example.ec.common.sql.SqlLoader;
import com.example.ec.order.dto.OrderDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** 受注（T_ORDER）のDao。 */
@Repository
public class OrderDao {

    /** 受注と顧客名。明細は含まない（OrderDetailDaoで取得する）。 */
    private static final RowMapper<OrderDto> ORDER_ROW_MAPPER = (rs, rowNum) -> new OrderDto(
            rs.getString("ORDER_NO"),
            rs.getObject("ORDER_DATE", LocalDate.class),
            rs.getString("CUSTOMER_CODE"),
            rs.getString("CUSTOMER_NAME"),
            rs.getString("DELIVERY_POSTAL_CODE"),
            rs.getString("REMARKS"),
            rs.getBigDecimal("TOTAL_AMOUNT"),
            rs.getString("ORDER_STATUS"),
            List.of());

    private static final RowMapper<String> ORDER_STATUS_ROW_MAPPER =
            (rs, rowNum) -> rs.getString("ORDER_STATUS");

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public OrderDao(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /** 受注を顧客名とともに取得する。明細は含まない（OrderDetailDaoで取得する）。 */
    public Optional<OrderDto> find(String orderNo) {
        return jdbcClient.sql(sqlLoader.load("SEL_ORDER"))
                .param(orderNo)
                .query(ORDER_ROW_MAPPER)
                .optional();
    }

    /** 受注を行ロックし、受注ステータスを返す。 */
    public Optional<String> findForUpdate(String orderNo) {
        return jdbcClient.sql(sqlLoader.load("SEL_ORDER_FOR_UPDATE"))
                .param(orderNo)
                .query(ORDER_STATUS_ROW_MAPPER)
                .optional();
    }

    /** 受注ステータスを更新する。 */
    public void updateStatus(String orderNo, String orderStatus) {
        jdbcClient.sql(sqlLoader.load("UPD_ORDER_STATUS"))
                .param(orderStatus)
                .param(orderNo)
                .update();
    }
}
