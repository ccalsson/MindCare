import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

const db = admin.firestore();
const badWords = ['palabra_bloqueada','ofensa1','ofensa2'];

function extractMentions(text: string): string[] {
  const regex = /@([A-Za-z0-9_\.\-]+)/g;
  const matches: string[] = [];
  let m;
  while ((m = regex.exec(text)) !== null) {
    matches.push(m[1]);
  }
  return matches;
}

async function resolveUserIds(usernames: string[]): Promise<string[]> {
  if (usernames.length === 0) return [];
  const out: string[] = [];
  for (const name of usernames) {
    const snap = await db.collection('users').where('displayName', '==', name).limit(1).get();
    if (!snap.empty) out.push(snap.docs[0].id);
  }
  return out;
}

async function sendPushToUsers(uids: string[], title: string, body: string) {
  if (uids.length === 0) return;
  const tokens: string[] = [];
  for (const uid of uids) {
    const devs = await db.collection('users').doc(uid).collection('devices').limit(20).get();
    devs.forEach((d) => { const t = d.data().token; if (t) tokens.push(t); });
  }
  if (tokens.length) {
    await admin.messaging().sendEachForMulticast({ notification: { title, body }, tokens });
  }
}

const WINDOW_MS = 30_000;
const MAX_MSG = 10;

export const onMessageCreate = functions.firestore
  .document('rooms/{roomId}/messages/{messageId}')
  .onCreate(async (snap, context) => {
    const roomId = context.params.roomId as string;
    const data = snap.data() as any;
    const text: string = data?.text || '';
    const senderId: string = data?.senderId;
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
    let needsReview = data?.needsReview === true;
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

