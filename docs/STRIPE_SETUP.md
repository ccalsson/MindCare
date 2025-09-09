Stripe setup (dev → prod)

Client-side (Flutter)
- Set `STRIPE_PUBLISHABLE_KEY` in `.env` (already referenced by StripeService).
- App initializes Stripe via `StripeService.initFromEnv()`; if missing, booking proceeds without charging (dev only).

Server-side (Firebase Functions) — recommended
- Configure secrets (do NOT commit keys):
  - `firebase functions:config:set stripe.secret=sk_live_xxx stripe.webhook_secret=whsec_xxx`
  - Redeploy functions (`firebase deploy --only functions`)
- Implement callable/HTTP endpoints to create PaymentIntents and return client_secret.
  - This repo already contains server/functions/src/stripe/webhooks.ts and stubs in index.ts.

Alternative server (Supabase or custom Node)
- Expose a secure endpoint that creates a PaymentIntent with Stripe secret key and returns the client_secret.
- The Flutter app should call that endpoint before presenting a payment sheet.

Local/dev behavior
- Without `STRIPE_PUBLISHABLE_KEY`, `StripeService.maybePay` returns true to unblock flow.
- Set `requirePayment: true` in production paths.

