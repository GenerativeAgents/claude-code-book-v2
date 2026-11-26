package com.example.ec.order.controller;

import com.example.ec.order.dto.OrderDto;
import com.example.ec.order.service.OrderCancelService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** ORD-020 受注取消。 */
@RestController
public class OrderCancelController {

    private final OrderCancelService orderCancelService;

    public OrderCancelController(OrderCancelService orderCancelService) {
        this.orderCancelService = orderCancelService;
    }

    @PostMapping("/orders/{orderNo}/cancel")
    public OrderDto cancel(@PathVariable String orderNo) {
        return orderCancelService.cancel(orderNo);
    }
}
