import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

<<<<<<< HEAD
// Logs writes on emotions entries to a private collection. In production this
// could instead stream to BigQuery for centralized analytics.
export const auditLog = functions.firestore
  .document('emotions/{uid}/entries/{entryId}')
  .onWrite(async (change, context) => {
    const action = !change.before.exists
      ? 'CREATE'
      : !change.after.exists
          ? 'DELETE'
          : 'UPDATE';
=======
export const auditLog = functions.firestore
  .document('emotions/{uid}/entries/{entryId}')
  .onWrite(async (change, context) => {
    const action = !change.before.exists ? 'CREATE' : !change.after.exists ? 'DELETE' : 'UPDATE';
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
    await admin.firestore().collection('audit_logs').add({
      uid: context.params.uid,
      entryId: context.params.entryId,
      action,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
    });
  });
