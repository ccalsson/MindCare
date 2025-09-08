import * as functions from 'firebase-functions/v1';
import * as admin from 'firebase-admin';

// Simple auth via functions config: seed.secret
function assertAuthorized(req: functions.https.Request) {
  const secret = process.env.SEED_SECRET || functions.config().seed?.secret;
  const token = (req.query.secret as string) || req.header('x-seed-secret');
  if (!secret || token !== secret) {
    throw new functions.https.HttpsError('permission-denied', 'Unauthorized');
  }
}

export const seedInitialData = functions.region('us-central1').https.onRequest(async (req, res) => {
  try {
    assertAuthorized(req);
    const db = admin.firestore();

    // Helper to create doc only if collection is empty
    const ensureDocs = async (
      colPath: string,
      docs: Array<Record<string, any>>,
    ) => {
      const snap = await db.collection(colPath).limit(1).get();
      if (!snap.empty) return { created: 0, skipped: true };
      const batch = db.batch();
      docs.forEach((d) => {
        const ref = db.collection(colPath).doc();
        batch.set(ref, { ...d, id: d.id || ref.id });
      });
      await batch.commit();
      return { created: docs.length, skipped: false };
    };

    // Project bucket name (best-effort default): <projectId>.appspot.com
    const projectId = process.env.GCLOUD_PROJECT || process.env.GCP_PROJECT || process.env.FIREBASE_CONFIG && JSON.parse(process.env.FIREBASE_CONFIG).projectId;
    const bucket = `${projectId}.appspot.com`;
    const audioUrl = (name: string) => `https://storage.googleapis.com/${bucket}/audio/${name}`;
    const imageUrl = (name: string) => `https://storage.googleapis.com/${bucket}/images/${name}`;

    // Seed meditation_sessions
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

    // Seed audio_resources
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

    // Seed premium_content (Timestamp for releaseDate)
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

    // Execute seeding
    const results: Record<string, unknown> = {};
    results['meditation_sessions'] = await ensureDocs('meditation_sessions', medSessions);
    results['audio_resources'] = await ensureDocs('audio_resources', audioResources);
    results['premium_content'] = await ensureDocs('premium_content', premiumContent);

    res.status(200).json({ ok: true, results });
  } catch (err: any) {
    console.error('Seed error', err);
    const status = err?.code === 'permission-denied' ? 403 : 500;
    res.status(status).json({ ok: false, error: err?.message || 'Unknown error' });
  }
});
