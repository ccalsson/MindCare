// Seed professionals and availability in Firestore using Admin SDK
// Usage:
//   export GOOGLE_APPLICATION_CREDENTIALS=server/keys/mindcare-sa.json
//   node server/seed_professionals.mjs <projectId>

import { createRequire } from 'module';
const require = createRequire(import.meta.url);
let admin;
try {
  admin = require('./functions/node_modules/firebase-admin');
} catch (e) {
  admin = require('firebase-admin');
}

const projectId = process.argv[2] || process.env.GOOGLE_CLOUD_PROJECT || process.env.GCLOUD_PROJECT;
if (!projectId) {
  console.error('Missing projectId. Pass as arg or set GCLOUD_PROJECT.');
  process.exit(1);
}

try {
  if (!admin.apps.length) {
    process.env.GOOGLE_CLOUD_PROJECT = projectId;
    admin.initializeApp();
  }
} catch (e) {
  admin.initializeApp();
}

const db = admin.firestore();

async function getOrCreateProfessional(data) {
  const snap = await db.collection('professionals').where('fullName', '==', data.fullName).limit(1).get();
  if (!snap.empty) {
    const doc = snap.docs[0];
    // Update minimal fields
    await doc.ref.set({ isActive: true, updatedAt: admin.firestore.FieldValue.serverTimestamp() }, { merge: true });
    return { id: doc.id, exists: true };
  }
  const ref = await db.collection('professionals').add({
    userId: data.userId || '',
    fullName: data.fullName,
    specialties: data.specialties || [],
    bio: data.bio || '',
    avatarUrl: data.avatarUrl || null,
    phone: data.phone || null,
    price: data.price || 0,
    isActive: true,
    rating: data.rating || 4.8,
    locations: data.locations || [],
    languages: data.languages || ['es'],
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
  });
  return { id: ref.id, exists: false };
}

async function setAvailability(proId, slots) {
  const doc = db.collection('availability').doc(proId);
  const payload = {
    slots: slots.map((s) => ({ dayOfWeek: s.dayOfWeek, start: s.start, end: s.end })),
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
  };
  await doc.set(payload, { merge: true });
}

async function main() {
  const pros = [
    {
      fullName: 'Dra. Ana Pérez',
      specialties: ['psicologia', 'ansiedad'],
      bio: 'Psicóloga clínica con 10 años de experiencia en manejo de ansiedad y mindfulness.',
      languages: ['es', 'en'],
      price: 60,
      rating: 4.9,
      locations: ['online'],
    },
    {
      fullName: 'Lic. Martín López',
      specialties: ['mindfulness', 'estrés'],
      bio: 'Especialista en estrés y hábitos saludables. Sesiones prácticas y enfocadas en resultados.',
      languages: ['es'],
      price: 50,
      rating: 4.7,
      locations: ['online'],
    },
  ];

  const results = [];
  for (const p of pros) {
    const { id, exists } = await getOrCreateProfessional(p);
    await setAvailability(id, [
      { dayOfWeek: 1, start: '10:00', end: '12:00' },
      { dayOfWeek: 3, start: '14:00', end: '18:00' },
    ]);
    results.push({ proId: id, name: p.fullName, existed: exists });
  }

  console.log(JSON.stringify({ ok: true, projectId, results }, null, 2));
}

main().then(() => process.exit(0)).catch((e) => {
  console.error(e);
  process.exit(1);
});
