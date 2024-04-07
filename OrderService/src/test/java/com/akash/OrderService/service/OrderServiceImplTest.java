package com.akash.OrderService.service;

import com.akash.OrderService.clients.PaymentClient;
import com.akash.OrderService.clients.ProductClient;
import com.akash.OrderService.entity.Order;
import com.akash.OrderService.external.PaymentResponse;
import com.akash.OrderService.model.OrderResponse;
import com.akash.OrderService.model.PaymentMode;
import com.akash.OrderService.repository.OrderRepository;
import com.akash.Product.Service.model.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

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