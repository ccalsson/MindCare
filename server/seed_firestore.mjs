// Seed Firestore directly using Firebase Admin SDK.
// Usage:
//   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/serviceAccount.json
//   node server/seed_firestore.mjs mindcare-745ca

import { createRequire } from 'module';
const require = createRequire(import.meta.url);
// Load firebase-admin from functions' node_modules to avoid reinstalling at root
const admin = require('./functions/node_modules/firebase-admin');

const projectId = process.argv[2] || process.env.GCLOUD_PROJECT || process.env.GCP_PROJECT;
if (!projectId) {
  console.error('Missing projectId. Pass as arg or set GCLOUD_PROJECT.');
  process.exit(1);
}

try {
  if (!admin.apps.length) {
    // Use ADC from GOOGLE_APPLICATION_CREDENTIALS; do not override project to avoid mismatch
    if (projectId) {
      process.env.GOOGLE_CLOUD_PROJECT = projectId;
    }
    admin.initializeApp();
  }
} catch (e) {
  admin.initializeApp();
}

const db = admin.firestore();

async function ensureDocs(colPath, docs) {
  const snap = await db.collection(colPath).limit(1).get();
  if (!snap.empty) return { created: 0, skipped: true };
  const batch = db.batch();
  for (const d of docs) {
    const ref = db.collection(colPath).doc();
    batch.set(ref, { ...d, id: d.id || ref.id });
  }
  await batch.commit();
  return { created: docs.length, skipped: false };
}

function bucketName() {
  return `${projectId}.appspot.com`;
}

const audioUrl = (name) => `https://storage.googleapis.com/${bucketName()}/audio/${name}`;
const imageUrl = (name) => `https://storage.googleapis.com/${bucketName()}/images/${name}`;

async function main() {
  const medSessions = [
    {
      title: 'Sueño profundo',
      description: 'Relajación guiada para mejorar el descanso nocturno',
      duration: 900,
      category: 'MeditationCategory.sleep',
      audioUrl: audioUrl('placeholder.mp3'),
      imageUrl: imageUrl('placeholder.png'),
      isPremium: false,
    },
    {
      title: 'Enfoque máximo',
      description: 'Meditación breve para maximizar tu concentración',
      duration: 600,
      category: 'MeditationCategory.focus',
      audioUrl: audioUrl('placeholder.mp3'),
      imageUrl: imageUrl('placeholder.png'),
      isPremium: false,
    },
    {
      title: 'Ansiedad bajo control',
      description: 'Respiraciones profundas y visualización calmante',
      duration: 720,
      category: 'MeditationCategory.anxiety',
      audioUrl: audioUrl('placeholder.mp3'),
      imageUrl: imageUrl('placeholder.png'),
      isPremium: false,
    },
    {
      title: 'Mindfulness inicial',
      description: 'Introducción práctica a la atención plena',
      duration: 480,
      category: 'MeditationCategory.beginner',
      audioUrl: audioUrl('placeholder.mp3'),
      imageUrl: imageUrl('placeholder.png'),
      isPremium: false,
    },
    {
      title: 'Masterclass Premium',
      description: 'Sesión avanzada para usuarios premium',
      duration: 1200,
      category: 'MeditationCategory.mindfulness',
      audioUrl: audioUrl('placeholder.mp3'),
      imageUrl: imageUrl('placeholder.png'),
      isPremium: true,
    },
  ];

  const audioResources = [
    {
      title: 'Bosque lluvioso',
      url: audioUrl('placeholder.mp3'),
      thumbnailUrl: imageUrl('placeholder.png'),
      duration: 1800,
      isPremium: false,
    },
    {
      title: 'Olas del mar',
      url: audioUrl('placeholder.mp3'),
      thumbnailUrl: imageUrl('placeholder.png'),
      duration: 1800,
      isPremium: false,
    },
    {
      title: 'Campanas tibetanas (Premium)',
      url: audioUrl('placeholder.mp3'),
      thumbnailUrl: imageUrl('placeholder.png'),
      duration: 1200,
      isPremium: true,
    },
  ];

  const premiumContent = [
    {
      title: 'Mindfulness para líderes',
      description: 'Masterclass exclusiva con expertos',
      type: 'ContentType.masterclass',
      tags: ['liderazgo', 'mindfulness'],
      metadata: { level: 'advanced' },
      releaseDate: admin.firestore.Timestamp.fromDate(new Date()),
    },
  ];

  const results = {};
  results['meditation_sessions'] = await ensureDocs('meditation_sessions', medSessions);
  results['audio_resources'] = await ensureDocs('audio_resources', audioResources);
  results['premium_content'] = await ensureDocs('premium_content', premiumContent);

  console.log(JSON.stringify({ ok: true, results }, null, 2));
}

main().then(() => process.exit(0)).catch((e) => {
  console.error(e);
  process.exit(1);
});
