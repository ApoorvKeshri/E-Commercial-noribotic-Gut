package in.nouribiome.store.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_orders", indexes = {
        @Index(name = "idx_customer_orders_status", columnList = "status"),
        @Index(name = "idx_customer_orders_created_at", columnList = "created_at")
})
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "razorpay_order_id", nullable = false, unique = true, length = 80)
    private String razorpayOrderId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "amount_paise", nullable = false)
    private Long amountPaise;

    @Column(nullable = false, length = 3)
    private String currency = "INR";

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 254)
    private String customerEmail;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "delivery_address", nullable = false, length = 1000)
    private String deliveryAddress;

    @Column(nullable = false, length = 30)
    private String status = "created";

    @Column(name = "razorpay_payment_id", length = 80)
    private String paymentId;

    @Column(name = "webhook_event", length = 80)
    private String webhookEvent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String value) { this.razorpayOrderId = value; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer value) { this.quantity = value; }
    public Long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(Long value) { this.amountPaise = value; }
    public String getCurrency() { return currency; }
    public void setCurrency(String value) { this.currency = value; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String value) { this.customerName = value; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String value) { this.customerEmail = value; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String value) { this.customerPhone = value; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String value) { this.deliveryAddress = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String value) { this.paymentId = value; }
    public String getWebhookEvent() { return webhookEvent; }
    public void setWebhookEvent(String value) { this.webhookEvent = value; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}