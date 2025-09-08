"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.auditLog = void 0;
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
exports.auditLog = functions.firestore
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
//# sourceMappingURL=auditLog.js.map