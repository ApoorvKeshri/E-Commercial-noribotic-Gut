package in.nouribiome.store.controller;

import in.nouribiome.store.entity.CustomerOrder;
import in.nouribiome.store.repository.CustomerOrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final CustomerOrderRepository orders;

    public AdminController(CustomerOrderRepository orders) {
        this.orders = orders;
    }

    @GetMapping("/orders")
    public List<Map<String, Object>> getOrders() {
        return orders.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toAdminView)
                .toList();
    }

    private Map<String, Object> toAdminView(CustomerOrder order) {
        return Map.ofEntries(
                Map.entry("id", order.getId()),
                Map.entry("razorpayOrderId", order.getRazorpayOrderId()),
                Map.entry("quantity", order.getQuantity()),
                Map.entry("amountPaise", order.getAmountPaise()),
                Map.entry("currency", order.getCurrency()),
                Map.entry("customer", Map.of(
                        "name", order.getCustomerName(),
                        "email", order.getCustomerEmail(),
                        "phone", order.getCustomerPhone(),
                        "address", order.getDeliveryAddress()
                )),
                Map.entry("status", order.getStatus()),
                Map.entry("paymentId", order.getPaymentId() == null ? "" : order.getPaymentId()),
                Map.entry("createdAt", order.getCreatedAt()),
                Map.entry("updatedAt", order.getUpdatedAt())
        );
    }
}