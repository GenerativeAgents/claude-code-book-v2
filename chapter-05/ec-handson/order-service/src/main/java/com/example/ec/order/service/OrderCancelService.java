package com.example.ec.order.service;

import com.example.ec.common.BusinessException;
import com.example.ec.order.client.CatalogClient;
import com.example.ec.order.dao.OrderDao;
import com.example.ec.order.dao.OrderDetailDao;
import com.example.ec.order.dto.OrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** ORD-020 受注取消。 */
@Service
public class OrderCancelService {

    private static final String STATUS_ACCEPTED = "10";
    private static final String STATUS_RESERVED = "20";
    private static final String STATUS_CANCELED = "90";

    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;
    private final CatalogClient catalogClient;

    public OrderCancelService(OrderDao orderDao, OrderDetailDao orderDetailDao, CatalogClient catalogClient) {
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
        this.catalogClient = catalogClient;
    }

    /**
     * 受注を取り消し、取消後の受注を返す。引当済の受注は在庫の引当も解除する。
     *
     * @throws BusinessException 受注が存在しない場合（E0111）、取り消せない受注ステータスの場合（E0112）
     */
    @Transactional
    public OrderDto cancel(String orderNo) {
        String status = orderDao.findForUpdate(orderNo)
                .orElseThrow(() -> BusinessException.notFound("E0111", orderNo));
        if (!STATUS_ACCEPTED.equals(status) && !STATUS_RESERVED.equals(status)) {
            throw BusinessException.conflict("E0112", status);
        }
        if (STATUS_RESERVED.equals(status)) {
            catalogClient.release(orderNo);
        }
        orderDao.updateStatus(orderNo, STATUS_CANCELED);

        OrderDto order = orderDao.find(orderNo).orElseThrow();
        return order.withDetails(orderDetailDao.findByOrderNo(orderNo));
    }
}
