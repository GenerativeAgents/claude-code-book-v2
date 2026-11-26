package com.example.ec.order.service;

import com.example.ec.common.BusinessException;
import com.example.ec.order.dao.OrderDao;
import com.example.ec.order.dao.OrderDetailDao;
import com.example.ec.order.dto.OrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** ORD-001 受注照会。 */
@Service
public class OrderQueryService {

    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;

    public OrderQueryService(OrderDao orderDao, OrderDetailDao orderDetailDao) {
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
    }

    /**
     * 受注を明細とともに取得する。
     *
     * @throws BusinessException 受注が存在しない場合（E0111）
     */
    @Transactional(readOnly = true)
    public OrderDto find(String orderNo) {
        OrderDto order = orderDao.find(orderNo)
                .orElseThrow(() -> BusinessException.notFound("E0111", orderNo));
        return order.withDetails(orderDetailDao.findByOrderNo(orderNo));
    }
}
