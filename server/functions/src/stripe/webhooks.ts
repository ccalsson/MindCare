import * as functions from 'firebase-functions';
import Stripe from 'stripe';

const stripe = new Stripe(functions.config().stripe.secret, {
  apiVersion: '2023-08-16',
});

export const stripeWebhook = functions.https.onRequest((req, res) => {
  const signature = req.headers['stripe-signature'] as string;
  const secret = functions.config().stripe.webhook_secret;
  let event: Stripe.Event;
  try {
    event = stripe.webhooks.constructEvent(req.rawBody, signature, secret);
  } catch (err) {
    res.status(400).send(`Webhook Error: ${(err as Error).message}`);
    return;
  }
  console.log('Received event', event.type);
  if (event.type === 'checkout.session.completed') {
    const session = event.data.object as any;
    const uid = session.metadata?.uid as string | undefined;
    const plan = session.metadata?.planTier || 'full';
    if (uid) {
      const db = require('firebase-admin').firestore() as import('firebase-admin').firestore.Firestore;
      db.collection('users').doc(uid).set({ planTier: plan }, { merge: true }).then(() => {
        console.log('planTier set for', uid, plan);
      }).catch((e: any) => console.error('Failed to set planTier', e));
    }
  }
  res.json({ received: true });
});
