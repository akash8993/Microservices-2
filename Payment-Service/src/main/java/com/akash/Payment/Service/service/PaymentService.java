package com.akash.Payment.Service.service;

import com.akash.Payment.Service.model.PaymentRequest;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);
}
