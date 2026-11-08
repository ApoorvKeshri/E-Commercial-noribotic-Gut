package in.nouribiome.store.controller;

import in.nouribiome.store.dto.CreateOrderRequest;
import in.nouribiome.store.dto.VerifyPaymentRequest;
import in.nouribiome.store.service.OrderService;
import in.nouribiome.store.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    private final RazorpayService razorpay;

    public OrderController(OrderService orderService, RazorpayService razorpay) {
        this.orderService = orderService;
        this.razorpay = razorpay;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        return Map.of("paymentConfigured", razorpay.isConfigured(), "currency", "INR");
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {
        return ResponseEntity.ok(orderService.verifyPayment(request));
    }
}