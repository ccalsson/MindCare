"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.mfaAdmin = void 0;
const functions = require("firebase-functions");
exports.mfaAdmin = functions.auth.user().afterCreate(async (user) => {
    var _a;
    const role = (_a = user.customClaims) === null || _a === void 0 ? void 0 : _a.role;
    if (role === 'admin' || role === 'pro') {
        // TODO: notify user to enroll MFA
        console.log(`User ${user.uid} requires MFA enrollment`);
    }
});
//# sourceMappingURL=mfaAdmin.js.map