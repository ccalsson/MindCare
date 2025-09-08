"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.migrateSegments = void 0;
const functions = require("firebase-functions");
const admin = require("firebase-admin");
exports.migrateSegments = functions.pubsub
    .topic('migrate-segments')
    .onPublish(async () => {
    const users = await admin.firestore().collection('users').get();
    for (const doc of users.docs) {
        // TODO: migrate user documents to new structure
        console.log('Migrating', doc.id);
    }
});
//# sourceMappingURL=migrateSegments.js.map