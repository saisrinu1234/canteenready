package com.example.canteen.payment.webhook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class WebhookController {

    private final PaymentWebhookService webhookService;

    public WebhookController(PaymentWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
                System.out.println("webhook triggered");

        webhookService.processWebhook(payload, signature);

        return ResponseEntity.ok("Webhook received");
    }
}