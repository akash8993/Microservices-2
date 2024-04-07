package com.akash.Payment.Service.service;

import com.akash.Payment.Service.model.PaymentRequest;
import com.akash.Payment.Service.model.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getDetails(String orderId);
}
