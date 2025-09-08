import * as functions from 'firebase-functions';
import { RateLimiterMemory } from 'rate-limiter-flexible';

const limiter = new RateLimiterMemory({ points: 5, duration: 60 });

export const rateLimit = functions.https.onRequest(async (req, res) => {
  try {
    await limiter.consume(req.ip);
    res.status(200).send('ok');
  } catch (e) {
    res.status(429).send('Too Many Requests');
  }
});
