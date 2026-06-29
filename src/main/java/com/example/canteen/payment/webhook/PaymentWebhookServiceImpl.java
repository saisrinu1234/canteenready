package com.example.canteen.payment.webhook;


import com.example.canteen.orders.OrderRepository;
import com.example.canteen.payment.Payment;
import com.example.canteen.payment.PaymentRepository;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentWebhookServiceImpl implements PaymentWebhookService {

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentWebhookServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void processWebhook(String payload, String signature) {

        try {

            boolean valid = Utils.verifyWebhookSignature(
                    payload,
                    signature,
                    webhookSecret);

            if (!valid) {
                throw new RuntimeException("Invalid Webhook Signature");
            }

            JSONObject json = new JSONObject(payload);

            String event = json.getString("event");

            JSONObject paymentEntity = json
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            String gatewayOrderId = paymentEntity.getString("order_id");
            String paymentId = paymentEntity.getString("id");
            String status = paymentEntity.getString("status");

            Payment payment = paymentRepository
                    .findByGatewayOrderId(gatewayOrderId)
                    .orElseThrow(() -> new RuntimeException("Payment Not Found"));

            payment.setPaymentId(paymentId);

            if ("captured".equals(status)) {

                payment.setPaymentStatus("SUCCESS");
                payment.getOrder().setPaymentStatus("SUCCESS");

            } else if ("failed".equals(status)) {

                payment.setPaymentStatus("FAILED");
                payment.getOrder().setPaymentStatus("FAILED");
            }

            paymentRepository.save(payment);
            orderRepository.save(payment.getOrder());

            System.out.println("Webhook Event : " + event);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

}