"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.seedInitialData = void 0;
const admin = require("firebase-admin");
// Initialize Admin once
try {
    admin.app();
}
catch (_) {
    admin.initializeApp();
}
var seed_1 = require("./maintenance/seed");
Object.defineProperty(exports, "seedInitialData", { enumerable: true, get: function () { return seed_1.seedInitialData; } });
//# sourceMappingURL=index_seed.js.map