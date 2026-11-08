# NouriBiome — Java + Spring Boot

## Requirements
- JDK 21
- PostgreSQL 15 or newer
- Maven 3.9+ (or use the Maven wrapper if one is added)
- Razorpay TEST credentials for payment testing

## Database setup
Create a local PostgreSQL database:
```sql
CREATE DATABASE nouribiome;
```

Set environment variables from `.env.example`. Spring Boot does not automatically load a .env file; configure these in your terminal, IDE run configuration, or hosting environment.

## Run
From this `backend` directory:
```bash
mvn spring-boot:run
```
Open http://localhost:8080

The existing storefront is served from `src/main/resources/static/index.html`.

## APIs
- GET `/api/config`
- POST `/api/create-order`
- POST `/api/verify-payment`
- POST `/api/razorpay-webhook`
- GET `/admin/orders` (HTTP Basic authentication)

## Razorpay webhook
Set the webhook URL to `https://YOUR-DOMAIN/api/razorpay-webhook` and configure the same webhook secret in `RAZORPAY_WEBHOOK_SECRET`. Subscribe to `payment.captured`, `payment.failed`, and `order.paid`.

## Notes
- The product price defaults to ₹799 as an illustrative amount. Confirm it before launch.
- Hibernate schema auto-update is enabled for local development. Use reviewed database migrations (for example, Flyway) and `ddl-auto: validate` for production.
- Use HTTPS, strong unique admin credentials, database backups, and Razorpay LIVE keys only after testing and merchant approval.
- This is a starter implementation; complete privacy, consumer, food-supplement, tax, shipping, refund, and FSSAI compliance before accepting real orders.
