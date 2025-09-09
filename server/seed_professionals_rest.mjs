import https from 'https';
import fs from 'fs';
import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { google } = require('googleapis');

const projectId = process.argv[2];
if (!projectId) throw new Error('Pass projectId');

async function getAccessToken() {
  const saPath = process.env.GOOGLE_APPLICATION_CREDENTIALS;
  if (!saPath || !fs.existsSync(saPath)) throw new Error('Missing GOOGLE_APPLICATION_CREDENTIALS');
  const auth = new google.auth.GoogleAuth({ keyFile: saPath, scopes: ['https://www.googleapis.com/auth/datastore'] });
  return await auth.getAccessToken();
}

async function run() {
  const token = await getAccessToken();
  const url = `https://firestore.googleapis.com/v1/projects/${projectId}/databases/(default)/documents:batchWrite`;
  const docs = [
    {
      name: 'professionals',
      fields: {
        userId: { stringValue: '' },
        fullName: { stringValue: 'Dra. Ana Pérez' },
        specialties: { arrayValue: { values: [{ stringValue: 'psicologia' }, { stringValue: 'ansiedad' }] } },
        bio: { stringValue: 'Psicóloga clínica con 10 años de experiencia en manejo de ansiedad y mindfulness.' },
        avatarUrl: { nullValue: null },
        phone: { nullValue: null },
        price: { doubleValue: 60 },
        isActive: { booleanValue: true },
        rating: { doubleValue: 4.9 },
        locations: { arrayValue: { values: [{ stringValue: 'online' }] } },
        languages: { arrayValue: { values: [{ stringValue: 'es' }, { stringValue: 'en' }] } }
      }
    },
    {
      name: 'professionals',
      fields: {
        userId: { stringValue: '' },
        fullName: { stringValue: 'Lic. Martín López' },
        specialties: { arrayValue: { values: [{ stringValue: 'mindfulness' }, { stringValue: 'estrés' }] } },
        bio: { stringValue: 'Especialista en estrés y hábitos saludables. Sesiones prácticas y enfocadas en resultados.' },
        avatarUrl: { nullValue: null },
        phone: { nullValue: null },
        price: { doubleValue: 50 },
        isActive: { booleanValue: true },
        rating: { doubleValue: 4.7 },
        locations: { arrayValue: { values: [{ stringValue: 'online' }] } },
        languages: { arrayValue: { values: [{ stringValue: 'es' }] } }
      }
    }
  ];

  // Create documents
  const writes = docs.map((d) => ({ update: { name: '', fields: d.fields } }));
  const body = JSON.stringify({ writes });
  const res = await new Promise((resolve, reject) => {
    const req = https.request(url, { method: 'POST', headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }, (r) => {
      let data = '';
      r.on('data', (c) => (data += c));
      r.on('end', () => resolve({ status: r.statusCode, body: data }));
    });
    req.on('error', reject);
    req.write(body);
    req.end();
  });
  if (res.status < 200 || res.status >= 300) throw new Error(res.body);
  const json = JSON.parse(res.body);
  const created = (json.writeResults || []).length;
  console.log('Created professionals:', created.length);
}

run().catch((e) => { console.error(e); process.exit(1); });
