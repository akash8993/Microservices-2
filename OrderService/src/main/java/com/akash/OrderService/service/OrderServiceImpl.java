package com.akash.OrderService.service;

import com.akash.OrderService.clients.ProductClient;
import com.akash.OrderService.entity.Order;
import com.akash.OrderService.model.OrderRequest;
import com.akash.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;
    @Override
    public Long placeOrder(OrderRequest orderRequest) {

        //First Create Order Entity with save data with status Order created
        //Product Service - Block Products (Reduce the quantity)
        //Payment Service -> Payments -> Sucess-> Complete, Else-> cancelled

        productClient.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());

        log.info("Placing Order Request {}", orderRequest);

        //1st step done
        Order order= Order.builder().productId(orderRequest.getProductId())
                        .amount(orderRequest.getAmount())
                        .quantity(orderRequest.getQuantity())
                    .orderStatus("CREATED")
                    .orderDate(Instant.now())
                    .build();



        Order placedOrder= orderRepository.save(order);

        log.info("Order Placed successfully with order id : {}",order.getId());
        return placedOrder.getId();
    }
}
