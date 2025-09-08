"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.stripeWebhook = void 0;
const functions = require("firebase-functions");
const stripe_1 = require("stripe");
const stripe = new stripe_1.default(functions.config().stripe.secret, {
    apiVersion: '2023-10-16',
});
exports.stripeWebhook = functions.https.onRequest((req, res) => {
    const signature = req.headers['stripe-signature'];
    const secret = functions.config().stripe.webhook_secret;
    let event;
    try {
        event = stripe.webhooks.constructEvent(req.rawBody, signature, secret);
    }
    catch (err) {
        res.status(400).send(`Webhook Error: ${err.message}`);
        return;
    }
    console.log('Received event', event.type);
    res.json({ received: true });
});
//# sourceMappingURL=webhooks.js.map