package in.nouribiome.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyPaymentRequest(
        @NotBlank @Size(max = 80) String razorpay_order_id,
        @NotBlank @Size(max = 80) String razorpay_payment_id,
        @NotBlank @Size(max = 256) String razorpay_signature
) {}