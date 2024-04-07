package com.akash.OrderService.service;

import com.akash.OrderService.clients.PaymentClient;
import com.akash.OrderService.clients.ProductClient;
import com.akash.OrderService.entity.Order;
import com.akash.OrderService.exception.CustomException;
import com.akash.OrderService.external.PaymentRequest;
import com.akash.OrderService.external.PaymentResponse;
import com.akash.OrderService.model.OrderRequest;
import com.akash.OrderService.model.OrderResponse;
import com.akash.OrderService.repository.OrderRepository;
import com.akash.Product.Service.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private RestTemplate restTemplate;

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
        log.info("Calling Payment Service to complete the payement");

        PaymentRequest paymentRequest= PaymentRequest.builder()
                .orderId(placedOrder.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getAmount())
                .build();
        String orderStatus= null;

        try{
            paymentClient.doPayment(paymentRequest);
            log.info("Payment done and changing the order status to done");
            orderStatus="PLACED";
        }catch(Exception e)
        {
            log.info("Error occured while doing payment and changing order status to payment fail");
            orderStatus= "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order Placed successfully with order id : {}",order.getId());
        return placedOrder.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        Order order=orderRepository.findById(orderId).orElseThrow(()-> new CustomException("Order Not found !","NOT_FOUND",404));

        //Now we will get the product Response here with rest Template
        log.info("Calling product service to fetch the product for id {}", order.getProductId());

        ProductResponse productResponse= restTemplate.getForObject("http://product-service:8080/product/findById/"+order.getProductId(), ProductResponse.class);

        log.info("Called product service to fetch the product for id {}", order.getProductId());

       //Here below we have converted the product response into ProductDetails
        OrderResponse.ProductDetails productDetails= OrderResponse.ProductDetails
                .builder()
                        .productName(productResponse.getProductName())
                                .price(productResponse.getPrice())
                                        .quantity(productResponse.getQuantity())
                                                .productId(productResponse.getProductId())
                .build();
        log.info("Getting Payment Information from the payment service");

        PaymentResponse paymentResponse= restTemplate.getForObject("http://payment-service/payment/getData/"+ order.getId(), PaymentResponse.class);

        log.info("Received payment information for the order Id {}", orderId);

        OrderResponse.PaymentDetails paymentDetails=
                OrderResponse.PaymentDetails
                        .builder()
                        .paymentDate(paymentResponse.getPaymentDate())
                        .paymentId(paymentResponse.getPaymentId())
                        .paymentStatus(paymentResponse.getStatus())
                        .paymentMode(paymentResponse.getPaymentMode())
                        .build();

        return OrderResponse.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
    }
}
