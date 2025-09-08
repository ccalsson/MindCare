import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

export const auditLog = functions.firestore
  .document('emotions/{uid}/entries/{entryId}')
  .onWrite(async (change, context) => {
    const action = !change.before.exists ? 'CREATE' : !change.after.exists ? 'DELETE' : 'UPDATE';
    await admin.firestore().collection('audit_logs').add({
      uid: context.params.uid,
      entryId: context.params.entryId,
      action,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
    });
  });
