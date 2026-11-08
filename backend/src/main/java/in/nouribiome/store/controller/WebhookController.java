package in.nouribiome.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.nouribiome.store.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebhookController {
    private final OrderService orderService;
    private final ObjectMapper mapper;

    public WebhookController(OrderService orderService, ObjectMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping("/razorpay-webhook")
    public ResponseEntity<Map<String, Object>> webhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        orderService.processWebhookEvent(rawBody, signature, mapper);
        return ResponseEntity.ok(Map.of("received", true));
    }
}