package in.nouribiome.store.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

@Service
public class RazorpayService {
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newHttpClient();

    @Value("${app.razorpay.key-id}") private String keyId;
    @Value("${app.razorpay.key-secret}") private String keySecret;
    @Value("${app.razorpay.webhook-secret}") private String webhookSecret;
    @Value("${app.razorpay.base-url}") private String baseUrl;

    public RazorpayService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public boolean isConfigured() {
        return keyId != null && !keyId.isBlank()
                && keySecret != null && !keySecret.isBlank();
    }

    public String getKeyId() {
        return keyId;
    }

    public JsonNode createOrder(long amountPaise, int quantity, String customerName, String customerPhone) {
        requireConfigured();
        Map<String, Object> payload = Map.of(
                "amount", amountPaise,
                "currency", "INR",
                "receipt", "NB" + System.currentTimeMillis(),
                "notes", Map.of(
                        "brand", "NouriBiome",
                        "product", "Daily Probiotic",
                        "quantity", String.valueOf(quantity),
                        "customer_name", customerName,
                        "customer_phone", customerPhone
                )
        );
        return send("POST", "/orders", mapper.valueToTree(payload));
    }

    public JsonNode fetchPayment(String paymentId) {
        requireConfigured();
        return send("GET", "/payments/" + paymentId, null);
    }

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        return constantTimeEquals(
                hmacHex(keySecret, orderId + "|" + paymentId),
                signature
        );
    }

    public boolean verifyWebhookSignature(String rawBody, String signature) {
        if (webhookSecret == null || webhookSecret.isBlank()) return false;
        return constantTimeEquals(hmacHex(webhookSecret, rawBody), signature);
    }

    private JsonNode send(String method, String path, JsonNode body) {
        try {
            String credentials = Base64.getEncoder().encodeToString(
                    (keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8)
            );

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Authorization", "Basic " + credentials)
                    .header("Content-Type", "application/json");

            if ("POST".equals(method)) {
                builder.POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
            } else {
                builder.GET();
            }

            HttpResponse<String> response = http.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            JsonNode json = mapper.readTree(response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String message = json.path("error").path("description").asText("Razorpay request failed");
                throw new IllegalStateException(message);
            }

            return json;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Razorpay request was interrupted", e);
        } catch (Exception e) {
            if (e instanceof IllegalStateException illegal) throw illegal;
            throw new IllegalStateException("Could not communicate with Razorpay", e);
        }
    }

    private void requireConfigured() {
        if (!isConfigured()) {
            throw new IllegalStateException("Razorpay keys are not configured on the server.");
        }
    }

    private static String hmacHex(String secret, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to calculate payment signature", e);
        }
    }

    private static boolean constantTimeEquals(String expected, String supplied) {
        if (expected == null || supplied == null) return false;
        return java.security.MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8)
        );
    }
}