# NouriBiome Store

NouriBiome's new backend is **Java 21 + Spring Boot + PostgreSQL**. The existing responsive storefront is included in the Spring Boot static resources.

## Project layout

- `backend/` — Java Spring Boot application, PostgreSQL persistence, Razorpay integration and admin endpoint.
- `backend/src/main/resources/static/index.html` — existing NouriBiome storefront.
- Root-level `server.js`, `package.json` and `public/` — previous Node.js version retained temporarily during migration. The Java application does not use them.

## Run the Java application locally

Requirements:
- JDK 21
- PostgreSQL 15+
- Maven 3.9+

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE nouribiome;
   ```
2. Configure the environment variables shown in [backend/.env.example](backend/.env.example). Spring Boot does not load `.env` automatically; set these in your terminal, IDE run configuration, or hosting environment.
3. From the `backend` directory run:
   ```bash
   mvn spring-boot:run
   ```
4. Open http://localhost:8080.

## API endpoints

- `GET /api/config`
- `POST /api/create-order`
- `POST /api/verify-payment`
- `POST /api/razorpay-webhook`
- `GET /admin/orders` — HTTP Basic authentication

See [backend/README.md](backend/README.md) for setup and API details.

## Important launch status

This is a development starter, not a live store. No production database, hosting, custom domain, Razorpay merchant activation, shipping integration or live payment setup has been completed. Product price defaults to ₹799 as an illustration and must be confirmed. Product formulation, strain identity, CFU count, label, claims and applicable FSSAI compliance must be verified before sale.

Before production, use database migrations rather than Hibernate auto-update, HTTPS, strong unique credentials, backups, rate limiting, reviewed privacy/consumer policies and tested payment/webhook flows. Never commit real secrets. Use Razorpay TEST keys until all checks are complete.

The WhatsApp contact in the storefront is the number supplied by the owner; confirm it is intended for public customer enquiries.
