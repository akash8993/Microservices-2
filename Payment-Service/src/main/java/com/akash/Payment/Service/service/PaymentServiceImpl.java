package com.akash.Payment.Service.service;

import com.akash.Payment.Service.entity.TransactionDetails;
import com.akash.Payment.Service.model.PaymentRequest;
import com.akash.Payment.Service.repository.PaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        TransactionDetails transactionDetails= TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .amount(paymentRequest.getAmount())
                .build();
        paymentRepository.save(transactionDetails);
       log.info("Transaction Completed with ID : {}", transactionDetails.getId());

        return transactionDetails.getId();
    }
}
