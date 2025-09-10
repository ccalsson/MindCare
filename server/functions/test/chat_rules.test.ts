import { initializeTestEnvironment, assertSucceeds, assertFails, RulesTestEnvironment } from '@firebase/rules-unit-testing';
import { readFileSync } from 'fs';

let testEnv: RulesTestEnvironment;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: 'demo-mindcare',
    firestore: { rules: readFileSync('../../firestore.rules', 'utf8') },
  });
});

after(async () => {
  await testEnv.cleanup();
});

describe('Chat rules', () => {
  it('teacher can read/write in teachers room', async () => {
    const ctx = testEnv.authenticatedContext('t1', { role: 'teacher', roles: ['teacher'] });
    const db = ctx.firestore();
    const roomRef = db.collection('rooms').doc('teachers');
    await assertSucceeds(roomRef.set({ name: 'Docentes', kind: 'teachers', isPublic: false, allowedRoles: ['teacher'], createdBy: 'sys', createdAt: new Date(), membersCount: 0 }));
    const msgRef = roomRef.collection('messages').doc();
    await assertSucceeds(msgRef.set({ senderId: 't1', text: 'hola', type: 'text', createdAt: new Date(), editedAt: null, deleted: false, mentions: [], attachments: [], needsReview: false }));
  });

  it('user without role cannot write in teachers room', async () => {
    const ctx = testEnv.authenticatedContext('u1', { role: 'student', roles: ['student'] });
    const db = ctx.firestore();
    const roomRef = db.collection('rooms').doc('teachers2');
    await roomRef.set({ name: 'Docentes', kind: 'teachers', isPublic: false, allowedRoles: ['teacher'], createdBy: 'sys', createdAt: new Date(), membersCount: 0 });
    const msgRef = roomRef.collection('messages').doc();
    await assertFails(msgRef.set({ senderId: 'u1', text: 'hola', type: 'text', createdAt: new Date(), editedAt: null, deleted: false, mentions: [], attachments: [], needsReview: false }));
  });
});

