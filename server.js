import express from "express";
import helmet from "helmet";
import rateLimit from "express-rate-limit";
import Razorpay from "razorpay";
import crypto from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import dotenv from "dotenv";

dotenv.config();
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const app = express();
const PORT = process.env.PORT || 3000;
const PRICE_PAISE = Number(process.env.PRODUCT_PRICE_PAISE || 79900);
const DATA_DIR = process.env.DATA_DIR || path.join(__dirname, "data");
const DB_FILE = path.join(DATA_DIR, "orders.json");
fs.mkdirSync(path.dirname(DB_FILE), { recursive: true });
const readOrders = () => { try { return JSON.parse(fs.readFileSync(DB_FILE, "utf8")); } catch { return []; } };
const writeOrders = (rows) => fs.writeFileSync(DB_FILE, JSON.stringify(rows, null, 2));
const razorpayReady = () => Boolean(process.env.RAZORPAY_KEY_ID && process.env.RAZORPAY_KEY_SECRET);
const razorpay = () => new Razorpay({ key_id: process.env.RAZORPAY_KEY_ID, key_secret: process.env.RAZORPAY_KEY_SECRET });

app.use(helmet({ contentSecurityPolicy: false }));
app.use("/api", rateLimit({ windowMs: 60_000, limit: 40, standardHeaders: "draft-7", legacyHeaders: false }));

// Webhook must receive the raw body for HMAC verification.
app.post("/api/razorpay-webhook", express.raw({ type: "application/json" }), (req, res) => {
  const secret = process.env.RAZORPAY_WEBHOOK_SECRET;
  if (!secret) return res.status(503).send("Webhook secret not configured");
  const signature = req.header("x-razorpay-signature") || "";
  const expected = crypto.createHmac("sha256", secret).update(req.body).digest("hex");
  const a = Buffer.from(signature), b = Buffer.from(expected);
  if (a.length !== b.length || !crypto.timingSafeEqual(a, b)) return res.status(400).send("Invalid signature");
  let event;
  try { event = JSON.parse(req.body.toString("utf8")); } catch { return res.status(400).send("Invalid JSON"); }
  const payment = event.payload?.payment?.entity;
  const orderId = payment?.order_id || event.payload?.order?.entity?.id;
  if (orderId && ["payment.captured", "order.paid", "payment.failed"].includes(event.event)) {
    const rows = readOrders();
    const item = rows.find(x => x.razorpayOrderId === orderId);
    if (item) {
      item.status = event.event === "payment.failed" ? "payment_failed" : "paid";
      item.webhookEvent = event.event;
      item.updatedAt = new Date().toISOString();
      writeOrders(rows);
    }
  }
  res.json({ received: true });
});

app.use(express.json({ limit: "20kb" }));
app.use(express.static(path.join(__dirname, "public")));
app.get("/api/config", (_req, res) => res.json({ paymentConfigured: razorpayReady(), currency: "INR" }));

app.post("/api/create-order", async (req, res) => {
  try {
    if (!razorpayReady()) return res.status(503).json({ error: "Razorpay keys are not configured on the server." });
    const { quantity, customer } = req.body || {};
    const qty = Number(quantity);
    if (!Number.isInteger(qty) || qty < 1 || qty > 10) return res.status(400).json({ error: "Invalid quantity." });
    if (!customer?.name || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(customer.email || "") || !customer.phone || !customer.address) return res.status(400).json({ error: "Please provide valid contact and delivery details." });
    const amount = PRICE_PAISE * qty;
    if (!Number.isSafeInteger(amount) || amount < 100) return res.status(400).json({ error: "Invalid order amount." });
    const order = await razorpay().orders.create({
      amount, currency: "INR", receipt: `NB${Date.now()}`,
      notes: { brand: "NouriBiome", product: "Daily Probiotic", quantity: String(qty), customer_name: String(customer.name).slice(0, 80), customer_phone: String(customer.phone).slice(0, 20) }
    });
    const rows = readOrders();
    rows.push({ razorpayOrderId: order.id, quantity: qty, amount, currency: "INR", customer: { name: customer.name, email: customer.email, phone: customer.phone, address: customer.address }, status: "created", createdAt: new Date().toISOString() });
    writeOrders(rows);
    res.json({ orderId: order.id, amount, currency: "INR", keyId: process.env.RAZORPAY_KEY_ID });
  } catch (e) {
    console.error("create-order:", e.message);
    res.status(500).json({ error: "Could not create payment order. Please try again." });
  }
});

app.post("/api/verify-payment", async (req, res) => {
  try {
    if (!razorpayReady()) return res.status(503).json({ error: "Payment provider is not configured." });
    const { razorpay_order_id, razorpay_payment_id, razorpay_signature } = req.body || {};
    const rows = readOrders();
    const order = rows.find(x => x.razorpayOrderId === razorpay_order_id);
    if (!order) return res.status(404).json({ error: "Order not found." });
    const expected = crypto.createHmac("sha256", process.env.RAZORPAY_KEY_SECRET).update(`${order.razorpayOrderId}|${razorpay_payment_id}`).digest("hex");
    const a = Buffer.from(String(razorpay_signature || "")), b = Buffer.from(expected);
    if (a.length !== b.length || !crypto.timingSafeEqual(a, b)) return res.status(400).json({ error: "Payment signature could not be verified." });
    const payment = await razorpay().payments.fetch(razorpay_payment_id);
    if (payment.order_id !== order.razorpayOrderId || Number(payment.amount) !== Number(order.amount) || payment.currency !== "INR") return res.status(400).json({ error: "Payment details do not match this order." });
    order.paymentId = razorpay_payment_id;
    order.status = payment.status === "captured" ? "paid" : payment.status;
    order.updatedAt = new Date().toISOString();
    writeOrders(rows);
    res.json({ verified: true, orderId: order.razorpayOrderId, status: order.status });
  } catch (e) {
    console.error("verify-payment:", e.message);
    res.status(500).json({ error: "Payment verification failed." });
  }
});

app.get("/admin/orders", (req, res) => {
  const user = process.env.ADMIN_USER, pass = process.env.ADMIN_PASSWORD;
  if (!user || !pass) return res.status(503).send("Set ADMIN_USER and ADMIN_PASSWORD.");
  const auth = req.headers.authorization || "";
  const [scheme, token] = auth.split(" ");
  let supplied = "";
  try { supplied = Buffer.from(token || "", "base64").toString(); } catch {}
  const [u, ...p] = supplied.split(":");
  if (scheme !== "Basic" || u !== user || p.join(":") !== pass) {
    res.set("WWW-Authenticate", 'Basic realm="NouriBiome Admin"');
    return res.status(401).send("Authentication required");
  }
  const safe = readOrders().map(({ customer, ...o }) => ({ ...o, customer: { name: customer?.name, email: customer?.email, phone: customer?.phone, address: customer?.address } }));
  const html = JSON.stringify(safe, null, 2).replace(/[&<>"']/g, c => ({ "&":"&amp;", "<":"&lt;", ">":"&gt;", '"':"&quot;", "'":"&#39;" }[c]));
  res.type("html").send(`<!doctype html><meta name="viewport" content="width=device-width"><title>NouriBiome orders</title><style>body{font:14px system-ui;background:#f7f5ef;color:#20372e;padding:24px}h1{font:38px Georgia}pre{white-space:pre-wrap;background:white;padding:18px;border:1px solid #ddd}</style><h1>NouriBiome orders</h1><p>Internal view. Handle customer data securely.</p><pre>${html}</pre>`);
});

app.use((req, res, next) => {
  if (req.path.startsWith("/api/") || req.path.startsWith("/admin/")) return next();
  if (req.method !== "GET") return res.status(404).send("Not found");
  res.sendFile(path.join(__dirname, "public", "index.html"));
});
app.listen(PORT, () => console.log(`NouriBiome server listening on ${PORT}`));
