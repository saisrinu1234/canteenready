package com.example.canteen.payment.webhook;


public interface PaymentWebhookService {

    void processWebhook(String payload, String signature);

}