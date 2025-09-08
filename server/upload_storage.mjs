import { createRequire } from 'module';
import path from 'node:path';
import fs from 'node:fs';

const require = createRequire(import.meta.url);
const admin = require('./functions/node_modules/firebase-admin');
const { Storage } = require('./functions/node_modules/@google-cloud/storage');

const projectId = process.argv[2];
if (!projectId) {
  console.error('Usage: node server/upload_storage.mjs <projectId>');
  process.exit(1);
}

// Init using ADC
try {
  if (!admin.apps.length) {
    process.env.GOOGLE_CLOUD_PROJECT = projectId;
    admin.initializeApp();
  }
} catch (e) {
  admin.initializeApp();
}

// Discover bucket
const storage = new Storage();
const [buckets] = await storage.getBuckets({ projectId });
const defaultCandidate = `${projectId}.appspot.com`;
const bucketName = (buckets.find(b => b.name === defaultCandidate)?.name) || (buckets[0]?.name) || defaultCandidate;
console.log(`Using bucket: ${bucketName}`);
const bucket = storage.bucket(bucketName);

async function upload(localPath, destination) {
  if (!fs.existsSync(localPath)) {
    throw new Error(`Local file not found: ${localPath}`);
  }
  await bucket.upload(localPath, { destination, gzip: false, public: false, validation: 'crc32c' });
  console.log(`Uploaded ${localPath} -> gs://${bucketName}/${destination}`);
}

async function main() {
  const root = path.resolve(process.cwd());
  await upload(path.join(root, 'assets/images/placeholder.png'), 'images/placeholder.png');
  await upload(path.join(root, 'assets/audio/placeholder.mp3'), 'audio/placeholder.mp3');
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
