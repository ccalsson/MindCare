import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import * as crypto from 'crypto';

<<<<<<< HEAD
// Salt should be provided via Secret Manager as ANON_SALT
const salt = process.env.ANON_SALT || functions.config().analytics?.salt || 'dev_salt';
=======
const salt = functions.config().analytics?.salt || 'dev_salt';
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)

export const anonymize = functions.firestore
  .document('metrics/{docId}')
  .onCreate(async (snap) => {
    const data = snap.data();
<<<<<<< HEAD
    const hash = crypto.createHmac('sha256', salt).update(data.uid).digest('hex');
=======
    const hash = crypto
      .createHmac('sha256', salt)
      .update(data.uid)
      .digest('hex');
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
    await admin.firestore().collection('analytics').add({
      ...data,
      uid: hash,
    });
  });
