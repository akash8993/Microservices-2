package com.akash.OrderService.service;

import com.akash.OrderService.clients.PaymentClient;
import com.akash.OrderService.clients.ProductClient;
import com.akash.OrderService.entity.Order;
import com.akash.OrderService.exception.CustomException;
import com.akash.OrderService.external.PaymentRequest;
import com.akash.OrderService.external.PaymentResponse;
import com.akash.OrderService.model.OrderRequest;
import com.akash.OrderService.model.OrderResponse;
import com.akash.OrderService.model.PaymentMode;
import com.akash.OrderService.repository.OrderRepository;
import com.akash.Product.Service.model.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {

    //Now we will mock all the services that are there in OrderServiceImpl class
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private RestTemplate restTemplate;

    //Below we have used Inject Mocks since we need to Inject Mocks in order service
    @InjectMocks
    OrderService orderService= new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_when_order_is_placed()
    {
        //Mocking

        //Now we will mock the repository bcoz we dont want to do the actual call on unit test

        Order order= getMockOrder();

        //Ye neeche wali is for order repo
        Mockito.when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        //Ye neeche wali is for rest Template to get the data
        Mockito.when(restTemplate.getForObject("http://product-service:8080/product/findById/"+order.getProductId(), ProductResponse.class))
                .thenReturn(getMockProductResponse());

        //3rd wala is for the Payment Response
        Mockito.when(restTemplate.getForObject("http://payment-service/payment/getData/"+ order.getId(), PaymentResponse.class))
                .thenReturn(getMockPaymentResponse());

        //Actual
      OrderResponse orderResponse= orderService.getOrderDetails(1);

        //Verification
        Mockito.verify(orderRepository, times(1)).findById(anyLong());

        Mockito.verify(restTemplate, times(1))
                .getForObject("http://product-service:8080/product/findById/"+order.getProductId(), ProductResponse.class);

        Mockito.verify(restTemplate, times(1))
                .getForObject("http://payment-service/payment/getData/"+ order.getId(), PaymentResponse.class);
        //Assert

        Assertions.assertNotNull(orderResponse);
        assertEquals(order.getId(),orderResponse.getId());

    }

    @Test
    @DisplayName("Get Orders - Failure Scenario")
    void test_When_Get_Order_Not_Found()
    {
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        //Assert
        CustomException exception= assertThrows(CustomException.class,
                ()-> orderService.getOrderDetails(1));
       assertEquals(null,exception.getErrorCode());
       assertEquals(404, exception.getStatus());

       verify(orderRepository, times(1)).findById(anyLong());

    }

    @Test
    @DisplayName("Place Order Success Scenario")
    void test_When_Place_Order_Success()
    {
        Order order= getMockOrder();
        OrderRequest orderRequest= getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        when(productClient.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(paymentClient.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId= orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2)).save(any());
        verify(productClient, times(1)).reduceQuantity(anyLong(),anyLong());
        verify(paymentClient, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    @Test
    @DisplayName("Place Order Failure Scenario")
    void test_When_Place_Order_PaymentFails_thenOrder_Placed()
    {
     Order order= getMockOrder();
     OrderRequest orderRequest= getMockOrderRequest();

     when(orderRepository.save(any(Order.class))).thenReturn(order);
     when(productClient.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

     when(paymentClient.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

     long orderId= orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2)).save(any());
        verify(productClient, times(1)).reduceQuantity(anyLong(),anyLong());
        verify(paymentClient, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);

    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .amount(100)
                .paymentMode(PaymentMode.CASH)
                .productId(1)
                .quantity(10)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .orderId(1)
                .paymentId(1)
                .paymentMode(PaymentMode.CASH)
                .paymentDate(Instant.now())
                .amount(1000)
                .status("DONE")
                .build();
    }

    private ProductResponse getMockProductResponse() {

        return ProductResponse.builder()
                .productId(2)
                .price(1000)
                .productName("IPHONE")
                .quantity(200)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1)
                .amount(1000)
                .quantity(200)
                .productId(2)
                .build();
    }

}