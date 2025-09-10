import * as functions from 'firebase-functions';
import { UserRecord } from 'firebase-functions/v1/auth';

export const mfaAdmin = functions.auth.user().onCreate(async (user: UserRecord) => {
  const role = user.customClaims?.role;
  if (role === 'admin' || role === 'pro') {
    // TODO: notify user to enroll MFA
    console.log(`User ${user.uid} requires MFA enrollment`);
  }
});
