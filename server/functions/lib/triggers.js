"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.onHighCraving = void 0;
exports.sendPush = sendPush;
const functions = require("firebase-functions");
const admin = require("firebase-admin");
async function sendPush(uid, title, body) {
    const db = admin.firestore();
    const tokensSnap = await db
        .collection('users')
        .doc(uid)
        .collection('devices')
        .get();
    const tokens = tokensSnap.docs.map((d) => d.data().token).filter(Boolean);
    if (!tokens.length)
        return;
    const message = {
        notification: { title, body: body || '' },
        tokens,
    };
    await admin.messaging().sendEachForMulticast(message);
}
exports.onHighCraving = functions.firestore
    .document('users/{uid}/journals/{jid}')
    .onCreate(async (snap, context) => {
    var _a;
    const data = snap.data();
    const craving = (_a = data === null || data === void 0 ? void 0 : data.craving) !== null && _a !== void 0 ? _a : 0;
    if (craving >= 8) {
        await sendPush(context.params.uid, '¿Necesitás ayuda ahora?', 'Tocá para ver opciones de ayuda inmediata');
    }
});
//# sourceMappingURL=triggers.js.map