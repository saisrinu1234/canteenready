package com.example.canteen.payment;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.canteen.orders.Order;
import com.example.canteen.orders.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String key;

    @Value("${razorpay.key.secret}")
    private String secret;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(OrderRepository orderRepository,
            PaymentRepository paymentRepository) {

        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CreatePaymentResponse createOrder(Long orderId) throws RazorpayException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        RazorpayClient client = new RazorpayClient(key, secret);

        JSONObject json = new JSONObject();

        json.put("amount", (int) (order.getTotalAmount() * 100));

        json.put("currency", "INR");

        json.put("receipt", "receipt_" + order.getId());

        com.razorpay.Order razorOrder = client.orders.create(json);

        Payment payment = new Payment();

        payment.setGatewayOrderId(razorOrder.get("id"));

        payment.setPaymentStatus("PENDING");

        payment.setOrder(order);

        paymentRepository.save(payment);

        return new CreatePaymentResponse(
                razorOrder.get("id"),
                razorOrder.get("amount"),
                key);
    }

    @Override
    public void verifyPayment(PaymentVerifyRequest request) throws RazorpayException {

        JSONObject json = new JSONObject();

        json.put("razorpay_order_id", request.getOrderId());
        json.put("razorpay_payment_id", request.getPaymentId());
        json.put("razorpay_signature", request.getSignature());

        boolean valid = Utils.verifyPaymentSignature(json, secret);

        if (!valid) {
            throw new RuntimeException("Invalid Signature");
        }

        Payment payment = paymentRepository
                .findByGatewayOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentId(request.getPaymentId());
        payment.setSignature(request.getSignature());
        payment.setPaymentStatus("SUCCESS");

        payment.getOrder().setPaymentStatus("SUCCESS");

        paymentRepository.save(payment);
        orderRepository.save(payment.getOrder());
    }

    @Override
    public void refundPayment(Long orderId) throws RazorpayException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Already refunded
        if ("REFUNDED".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Payment has already been refunded");
        }

        // Payment not successful
        if (!"SUCCESS".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Only successful payments can be refunded");
        }

        RazorpayClient client = new RazorpayClient(key, secret);

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", (int) (order.getTotalAmount() * 100));

        client.payments.refund(payment.getPaymentId(), refundRequest);

        payment.setPaymentStatus("REFUNDED");
        order.setPaymentStatus("REFUNDED");

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

}