"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.dailyCoach = void 0;
const functions = require("firebase-functions");
const admin = require("firebase-admin");
// dailyCoach: resumes last journals and creates events with payload.coachMsg (deterministic template)
exports.dailyCoach = functions.pubsub
    .schedule('0 9 * * *')
    .timeZone('America/Argentina/Buenos_Aires')
    .onRun(async () => {
    const db = admin.firestore();
    const usersSnap = await db.collection('users').get();
    const batch = db.batch();
    usersSnap.forEach((userDoc) => {
        batch.create(db.collection('users').doc(userDoc.id).collection('events').doc(), {
            type: 'coach',
            payload: {
                // NOTE: No raw data sent to LLM. Deterministic template summarization.
                coachMsg: 'Recordatorio diario: respirá profundo (4-7-8), registrá tu estado y usá tu plan. Si necesitás ayuda, tocá "Ayuda ahora".',
            },
            createdAt: new Date().toISOString(),
        });
    });
    await batch.commit();
    return null;
});
//# sourceMappingURL=ai.js.map