"use strict";
var _a;
Object.defineProperty(exports, "__esModule", { value: true });
exports.anonymize = void 0;
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const crypto = require("crypto");
const salt = ((_a = functions.config().analytics) === null || _a === void 0 ? void 0 : _a.salt) || 'dev_salt';
exports.anonymize = functions.firestore
    .document('metrics/{docId}')
    .onCreate(async (snap) => {
    const data = snap.data();
    const hash = crypto
        .createHmac('sha256', salt)
        .update(data.uid)
        .digest('hex');
    await admin.firestore().collection('analytics').add(Object.assign(Object.assign({}, data), { uid: hash }));
});
//# sourceMappingURL=anonymize.js.map