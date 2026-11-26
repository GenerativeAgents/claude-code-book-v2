package com.example.ec.order.controller;

import com.example.ec.order.dto.OrderDto;
import com.example.ec.order.service.OrderQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** ORD-001 受注照会。 */
@RestController
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    public OrderQueryController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping("/orders/{orderNo}")
    public OrderDto find(@PathVariable String orderNo) {
        return orderQueryService.find(orderNo);
    }
}
