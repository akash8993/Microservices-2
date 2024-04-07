package com.akash.OrderService.clients;

import com.akash.OrderService.external.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface PaymentClient {

    @PostMapping("/doPayment")
    ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);
}
