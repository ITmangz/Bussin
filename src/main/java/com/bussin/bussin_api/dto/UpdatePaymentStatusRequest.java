package com.bussin.bussin_api.dto;

import com.bussin.bussin_api.entity.PaymentStatus;

import jakarta.validation.constraints.NotNull;

public class UpdatePaymentStatusRequest {

    @NotNull
    private PaymentStatus paymentStatus;

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
