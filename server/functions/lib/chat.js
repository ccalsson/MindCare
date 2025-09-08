"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.onMessageCreate = void 0;
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();
const badWords = ['palabra_bloqueada', 'ofensa1', 'ofensa2'];
function extractMentions(text) {
    const regex = /@([A-Za-z0-9_\.\-]+)/g;
    const matches = [];
    let m;
    while ((m = regex.exec(text)) !== null) {
        matches.push(m[1]);
    }
    return matches;
}
async function resolveUserIds(usernames) {
    if (usernames.length === 0)
        return [];
    const out = [];
    for (const name of usernames) {
        const snap = await db.collection('users').where('displayName', '==', name).limit(1).get();
        if (!snap.empty)
            out.push(snap.docs[0].id);
    }
    return out;
}
async function sendPushToUsers(uids, title, body) {
    if (uids.length === 0)
        return;
    const tokens = [];
    for (const uid of uids) {
        const devs = await db.collection('users').doc(uid).collection('devices').limit(20).get();
        devs.forEach((d) => { const t = d.data().token; if (t)
            tokens.push(t); });
    }
    if (tokens.length) {
        await admin.messaging().sendEachForMulticast({ notification: { title, body }, tokens });
    }
}
const WINDOW_MS = 30000;
const MAX_MSG = 10;
exports.onMessageCreate = functions.firestore
    .document('rooms/{roomId}/messages/{messageId}')
    .onCreate(async (snap, context) => {
    const roomId = context.params.roomId;
    const data = snap.data();
    const text = (data === null || data === void 0 ? void 0 : data.text) || '';
    const senderId = data === null || data === void 0 ? void 0 : data.senderId;
    const createdAt = new Date();
    // Mentions
    const names = extractMentions(text);
    const mentionUids = await resolveUserIds(names);
    if (mentionUids.length) {
        await snap.ref.update({ mentions: admin.firestore.FieldValue.arrayUnion(...mentionUids) });
        await sendPushToUsers(mentionUids, 'Te mencionaron', text.slice(0, 100));
    }
    // Moderate bad words
    const lowered = text.toLowerCase();
    let needsReview = (data === null || data === void 0 ? void 0 : data.needsReview) === true;
    if (badWords.some((w) => lowered.includes(w))) {
        needsReview = true;
    }
    if (needsReview) {
        await snap.ref.update({ needsReview: true });
    }
    // Rate limit per user/room
    const windowStart = Date.now() - WINDOW_MS;
    const recent = await db
        .collection('rooms').doc(roomId)
        .collection('messages')
        .where('senderId', '==', senderId)
        .where('createdAt', '>', admin.firestore.Timestamp.fromMillis(windowStart))
        .get();
    if (recent.size > MAX_MSG) {
        await snap.ref.update({ needsReview: true });
        // Block further writes for 30s
        await db.collection('rooms').doc(roomId).collection('memberships').doc(senderId)
            .set({ blockedUntil: admin.firestore.Timestamp.fromMillis(Date.now() + WINDOW_MS) }, { merge: true });
    }
    // Topic push (optional)
    await admin.messaging().sendToTopic(`room_${roomId}`, {
        notification: { title: 'Nuevo mensaje', body: text.slice(0, 120) },
    });
    // Update room lastAt/lastMessage if not reviewed
    await db.collection('rooms').doc(roomId).set({
        lastMessage: needsReview ? 'Mensaje enviado' : (text || '[adjunto]'),
        lastAt: createdAt,
    }, { merge: true });
});
//# sourceMappingURL=chat.js.map