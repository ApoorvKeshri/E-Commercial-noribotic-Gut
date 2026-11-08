# NouriBiome commercial launch starter

Responsive Node/Express storefront starter with WhatsApp support and Razorpay Standard Checkout scaffolding. Includes server-side order creation, HMAC signature verification, payment-status lookup, webhook verification and a basic-auth protected `/admin/orders` view.

## Important launch status
This repository is a starter, not a live store. No hosting deployment, custom domain, Razorpay merchant activation, shipping integration or production database is connected. Product price defaults to ₹799 as an illustration and must be confirmed. Product formulation, strain identity, CFU count, label, claims and FSSAI compliance must be verified before sale.

## Run locally
1. Install Node.js 20 or newer.
2. Copy `.env.example` to `.env`.
3. Add Razorpay TEST credentials in `.env`; never commit `.env` or expose the key secret in browser code.
4. Set a long, unique `ADMIN_PASSWORD`.
5. Run `npm install` and `npm start`.
6. Visit `http://localhost:3000`.

## Deploy on Render
1. Create a new Blueprint deployment in Render and select this repository.
2. Set all environment variables in Render. Do not paste secrets into GitHub.
3. Deploy and test using the generated `onrender.com` URL and Razorpay TEST mode.
4. The blueprint sets `DATA_DIR=/var/data`. Attach a persistent disk mounted at `/var/data`, or replace JSON storage with a managed database before accepting real orders. Render's default filesystem is ephemeral.
5. Configure Razorpay webhook URL as `https://YOUR-HOST/api/razorpay-webhook` and subscribe to `payment.captured`, `payment.failed` and `order.paid`.
6. Add `nouribiome.in` as a custom domain in Render after registering it, then follow Render's DNS instructions.

## Before accepting real orders
- Complete Razorpay merchant KYC and settlement setup; test checkout, signature verification and webhook delivery.
- Use LIVE keys only after successful testing and approval.
- Confirm product classification and applicable FSSAI licences for manufacturer, marketer, storage and e-commerce activities.
- Obtain a compliant final label with strain details, viable count at end of shelf life, serving instructions, storage, warnings, allergens, batch/expiry and responsible business details.
- Have every health claim reviewed against applicable FSSAI rules and evidence. Do not claim to prevent, treat or cure disease.
- Add legally reviewed Terms, Privacy, Shipping, Cancellation/Returns/Refunds, Contact and Grievance Officer details.
- Confirm MRP, taxes, shipping charges, inventory, fulfilment and customer support.
- Confirm trademark clearance and domain availability before advertising or packaging.

The WhatsApp contact in the storefront is the number supplied by the owner; confirm it is intended for public customer enquiries.
