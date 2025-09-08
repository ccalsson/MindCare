import { readFileSync } from 'fs';
import * as firebase from '@firebase/rules-unit-testing';
import { assertFails, assertSucceeds } from '@firebase/rules-unit-testing';

const projectId = 'mindcare-test';

function getDb(auth?: any) {
  return firebase.initializeTestApp({ projectId, auth }).firestore();
}

before(async () => {
  await firebase.loadFirestoreRules({
    projectId,
    rules: readFileSync('../../firestore.rules', 'utf8'),
  });
});

after(async () => {
  await Promise.all(firebase.apps().map((app) => app.delete()));
});

describe('Firestore security', () => {
  it('allows owner to read own emotion', async () => {
    const db = getDb({ uid: 'u1', token: { role: 'user' } });
    const ref = db.collection('emotions').doc('u1').collection('entries').doc('e1');
    await assertSucceeds(ref.get());
  });

  it('denies other users', async () => {
    const db = getDb({ uid: 'u1', token: { role: 'user' } });
    const ref = db.collection('emotions').doc('u2').collection('entries').doc('e1');
    await assertFails(ref.get());
  });
});
