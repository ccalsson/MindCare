import { readFileSync } from 'fs';
<<<<<<< HEAD
import {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
  RulesTestEnvironment,
} from '@firebase/rules-unit-testing';

const projectId = 'mindcare-test';
let testEnv: RulesTestEnvironment;

function getDb(auth?: { uid: string; token: any }) {
  if (!auth) {
    return testEnv.unauthenticatedContext().firestore();
  }
  return testEnv
    .authenticatedContext(auth.uid, auth.token)
    .firestore();
}

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId,
    firestore: {
      rules: readFileSync('../../firestore.rules', 'utf8'),
    },
=======
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
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
  });
});

after(async () => {
<<<<<<< HEAD
  await testEnv.cleanup();
=======
  await Promise.all(firebase.apps().map((app) => app.delete()));
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
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
<<<<<<< HEAD

  it('allows owner billing access', async () => {
    const db = getDb({ uid: 'u1', token: { role: 'user' } });
    const ref = db.collection('billing').doc('u1').collection('subscriptions').doc('s1');
    await assertSucceeds(ref.get());
  });

  it('denies non-owner billing access', async () => {
    const db = getDb({ uid: 'u2', token: { role: 'user' } });
    const ref = db.collection('billing').doc('u1').collection('subscriptions').doc('s1');
    await assertFails(ref.get());
  });

  it('allows public read of professionals', async () => {
    const db = getDb();
    const ref = db.collection('professionals').doc('p1');
    await assertSucceeds(ref.get());
  });

  it('denies non-admin write to professionals', async () => {
    const db = getDb({ uid: 'u1', token: { role: 'user' } });
    const ref = db.collection('professionals').doc('p1');
    await assertFails(ref.set({ foo: 'bar' }));
  });

  it('allows admin write to professionals', async () => {
    const db = getDb({ uid: 'a1', token: { role: 'admin' } });
    const ref = db.collection('professionals').doc('p1');
    await assertSucceeds(ref.set({ foo: 'bar' }));
  });
=======
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
});
