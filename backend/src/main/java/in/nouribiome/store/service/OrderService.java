package in.nouribiome.store.service;

import com.fasterxml.jackson.databind.JsonNode;
import in.nouribiome.store.dto.CreateOrderRequest;
import in.nouribiome.store.dto.VerifyPaymentRequest;
import in.nouribiome.store.entity.CustomerOrder;
import in.nouribiome.store.repository.CustomerOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OrderService {
    private final CustomerOrderRepository orders;
    private final RazorpayService razorpay;

    @Value("${app.product.price-paise}")
    private long pricePaise;

    public OrderService(CustomerOrderRepository orders, RazorpayService razorpay) {
        this.orders = orders;
        this.razorpay = razorpay;
    }

    @Transactional
    public Map<String, Object> createOrder(CreateOrderRequest request) {
        int quantity = request.quantity();
        var customer = request.customer();
        long amount = Math.multiplyExact(pricePaise, quantity);

        if (amount < 100) throw new IllegalArgumentException("Invalid order amount.");

        JsonNode razorpayOrder = razorpay.createOrder(
                amount, quantity, customer.name(), customer.phone()
        );

        CustomerOrder order = new CustomerOrder();
        order.setRazorpayOrderId(razorpayOrder.path("id").asText());
        order.setQuantity(quantity);
        order.setAmountPaise(amount);
        order.setCurrency("INR");
        order.setCustomerName(customer.name().trim());
        order.setCustomerEmail(customer.email().trim());
        order.setCustomerPhone(customer.phone().trim());
        order.setDeliveryAddress(customer.address().trim());
        order.setStatus("created");

        orders.save(order);

        return Map.of(
                "orderId", order.getRazorpayOrderId(),
                "amount", amount,
                "currency", "INR",
                "keyId", razorpay.getKeyId()
        );
    }

    @Transactional
    public Map<String, Object> verifyPayment(VerifyPaymentRequest request) {
        CustomerOrder order = orders.findByRazorpayOrderId(request.razorpay_order_id())
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        if (!razorpay.verifyPaymentSignature(
                request.razorpay_order_id(),
                request.razorpay_payment_id(),
                request.razorpay_signature())) {
            throw new IllegalArgumentException("Payment signature could not be verified.");
        }

        JsonNode payment = razorpay.fetchPayment(request.razorpay_payment_id());

        if (!request.razorpay_order_id().equals(payment.path("order_id").asText())
                || order.getAmountPaise() != payment.path("amount").asLong()
                || !"INR".equals(payment.path("currency").asText())) {
            throw new IllegalArgumentException("Payment details do not match this order.");
        }

        String status = payment.path("status").asText();
        order.setPaymentId(request.razorpay_payment_id());
        order.setStatus("captured".equals(status) ? "paid" : status);
        orders.save(order);

        return Map.of(
                "verified", true,
                "orderId", order.getRazorpayOrderId(),
                "status", order.getStatus()
        );
    }

    @Transactional
    public void processWebhookEvent(String rawBody, String signature, com.fasterxml.jackson.databind.ObjectMapper mapper) {
        if (!razorpay.verifyWebhookSignature(rawBody, signature)) {
            throw new IllegalArgumentException("Invalid webhook signature.");
        }

        try {
            JsonNode event = mapper.readTree(rawBody);
            String eventName = event.path("event").asText();
            JsonNode payment = event.path("payload").path("payment").path("entity");
            String orderId = payment.path("order_id").asText();

            if (orderId.isBlank()) {
                orderId = event.path("payload").path("order").path("entity").path("id").asText();
            }

            if (orderId.isBlank()) return;

            CustomerOrder order = orders.findByRazorpayOrderId(orderId).orElse(null);
            if (order == null) return;

            if ("payment.captured".equals(eventName) || "order.paid".equals(eventName)) {
                order.setStatus("paid");
                String paymentId = payment.path("id").asText();
                if (!paymentId.isBlank()) order.setPaymentId(paymentId);
            } else if ("payment.failed".equals(eventName)) {
                order.setStatus("payment_failed");
            } else {
                return;
            }

            order.setWebhookEvent(eventName);
            orders.save(order);
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("Invalid webhook JSON.", e);
        }
    }
}