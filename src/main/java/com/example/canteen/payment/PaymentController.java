package com.example.canteen.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/payment")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create/{orderId}")
    public CreatePaymentResponse createPayment(
            @PathVariable Long orderId) throws Exception {

        return paymentService.createOrder(orderId);

    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody PaymentVerifyRequest request)
            throws Exception {

        paymentService.verifyPayment(request);

        return "Payment Verified Successfully";
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<String> refund(@PathVariable Long orderId) {

        try {
            paymentService.refundPayment(orderId);
            return ResponseEntity.ok("Refund Successful");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RazorpayException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}