# MindCare

Setup rápido (5 minutos)

- Requisitos: Flutter 3.x, Firebase CLI, Node 18+, cuenta de Firebase y Supabase.
- Copiá variables: `cp .env.sample .env` y completá valores.
- Android: minSdk = 26 (ya configurado en `android/app/build.gradle.kts`).

Firebase (prod)
- Firestore: creado y con seed mínimo.
- Storage: reglas desplegadas; assets subidos (placeholder).
- Para re-seed + assets: exportá `GOOGLE_APPLICATION_CREDENTIALS` y corré `tools/seed_and_assets.sh`.

Supabase
- Esquema y seed inicial listos.
- Aplicar (si fuese necesario):
  - `export NODE_PATH=server/functions/node_modules:$NODE_PATH`
  - `node server/sql/apply_schema.mjs "postgresql://..."`
  - `node server/sql/apply_seed.mjs "postgresql://..."`
  - Demo (pros+slots): `node server/sql/apply_seed_demo.mjs "postgresql://..."`

CI/CD
- Workflow en `.github/workflows/flutter-ci.yml` con analyze/test/build web.

Entorno
- `.env.sample` contiene claves de Firebase, Supabase, Stripe, etc.
- No comitear `.env` ni claves; el repo ignora `server/keys/*.json`.

Módulo Adicciones (nuevo)
- Rutas GoRouter: `/addictions`, `/addictions/screening/:instrument/:version`, `/addictions/plan`, `/addictions/checkin`, `/addictions/progress`, `/addictions/exercises`, `/addictions/help`.
- Modelos y repos: `lib/features/addictions/`.
- Reglas Firestore actualizadas para subcolecciones en `users/{uid}`.
