package com.akash.Payment.Service.controller;

import com.akash.Payment.Service.model.PaymentMode;
import com.akash.Payment.Service.model.PaymentRequest;
import com.akash.Payment.Service.model.PaymentResponse;
import com.akash.Payment.Service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;

    @PostMapping("/doPayment")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest)
    {
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest), HttpStatus.OK);
    }

    @GetMapping("/getData/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable("orderId") String orderId)
    {
        return new ResponseEntity<>(paymentService.getDetails(orderId), HttpStatus.OK);
    }
}
