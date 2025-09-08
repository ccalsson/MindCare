import * as functions from 'firebase-functions';
import Stripe from 'stripe';

const stripe = new Stripe(functions.config().stripe.secret, {
<<<<<<< HEAD
  apiVersion: '2023-08-16',
=======
  apiVersion: '2023-10-16',
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
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
<<<<<<< HEAD
  switch (event.type) {
    case 'checkout.session.completed':
    case 'invoice.paid':
    case 'customer.subscription.updated':
    case 'customer.subscription.deleted':
      console.log('Handled', event.type);
      break;
    default:
      console.log('Unhandled event', event.type);
  }
=======
  console.log('Received event', event.type);
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
  res.json({ received: true });
});
