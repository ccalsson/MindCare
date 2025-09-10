"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.stripeWebhook = void 0;
const functions = require("firebase-functions");
const stripe_1 = require("stripe");
const stripe = new stripe_1.default(functions.config().stripe.secret, {
    apiVersion: '2023-08-16',
});
exports.stripeWebhook = functions.https.onRequest((req, res) => {
    var _a, _b;
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
    if (event.type === 'checkout.session.completed') {
        const session = event.data.object;
        const uid = (_a = session.metadata) === null || _a === void 0 ? void 0 : _a.uid;
        const plan = ((_b = session.metadata) === null || _b === void 0 ? void 0 : _b.planTier) || 'full';
        if (uid) {
            const db = require('firebase-admin').firestore();
            db.collection('users').doc(uid).set({ planTier: plan }, { merge: true }).then(() => {
                console.log('planTier set for', uid, plan);
            }).catch((e) => console.error('Failed to set planTier', e));
        }
    }
    res.json({ received: true });
});
//# sourceMappingURL=webhooks.js.map