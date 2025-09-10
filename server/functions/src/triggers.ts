import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

async function sendPush(uid: string, title: string, body?: string) {
  const db = admin.firestore();
  const tokensSnap = await db
    .collection('users')
    .doc(uid)
    .collection('devices')
    .get();
  const tokens = tokensSnap.docs.map((d) => d.data().token).filter(Boolean);
  if (!tokens.length) return;
  const message = {
    notification: { title, body: body || '' },
    tokens,
  } as any;
  await admin.messaging().sendEachForMulticast(message);
}

export const onHighCraving = functions.firestore
  .document('users/{uid}/journals/{jid}')
  .onCreate(async (snap, context) => {
    const data = snap.data() as any;
    const craving = data?.craving ?? 0;
    if (craving >= 8) {
      await sendPush(context.params.uid, '¿Necesitás ayuda ahora?', 'Tocá para ver opciones de ayuda inmediata');
    }
  });

export { sendPush };

