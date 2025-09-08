"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.rateLimit = void 0;
const functions = require("firebase-functions");
const rate_limiter_flexible_1 = require("rate-limiter-flexible");
const limiter = new rate_limiter_flexible_1.RateLimiterMemory({ points: 5, duration: 60 });
exports.rateLimit = functions.https.onRequest(async (req, res) => {
    try {
        await limiter.consume(req.ip);
        res.status(200).send('ok');
    }
    catch (e) {
        res.status(429).send('Too Many Requests');
    }
});
//# sourceMappingURL=rateLimit.js.map