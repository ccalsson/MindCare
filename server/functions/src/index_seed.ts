import * as admin from 'firebase-admin';

// Initialize Admin once
try {
  admin.app();
} catch (_) {
  admin.initializeApp();
}

export { seedInitialData } from './maintenance/seed';

