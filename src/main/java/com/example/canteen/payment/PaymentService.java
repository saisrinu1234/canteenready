package com.example.canteen.payment;

import com.razorpay.RazorpayException;

public interface PaymentService {

    CreatePaymentResponse createOrder(Long orderId) throws Exception;
    void verifyPayment(PaymentVerifyRequest request) throws RazorpayException;
    void refundPayment(Long orderId) throws RazorpayException;

}