# Repository Guidelines

## Project Structure & Module Organization
- Código Flutter en `lib/` (p. ej., `lib/screens/`, `lib/services/`, `lib/models/`, `lib/modules/`). Punto de entrada: `lib/main.dart`.
- Tests en `test/` (ejemplos deshabilitados en `test_disabled/`).
- Assets en `assets/` definidos en `pubspec.yaml`.
- Backend en `server/`: Functions en `server/functions/`, SQL/seed en `server/sql/`.
- Plataformas: `android/`, `ios/`, `web/`, `macos/`, `linux/`, `windows/`.
- Config/docs: `.env.sample`, `firestore.rules`, `storage.rules`, `docs/` y archivos de seguridad en la raíz.

## Build, Test, and Development Commands
- Flutter
  - `flutter pub get` — instala dependencias.
  - `flutter analyze` — análisis estático (también en CI).
  - `flutter test --no-pub` — ejecuta tests.
  - `flutter run -d chrome|ios|android` — corre local.
  - `flutter build web --release` — build web prod.
- Functions/Server (Node 18)
  - `cd server/functions && npm ci && npm run build` — compila TypeScript.
  - `npm run serve` — emuladores Firebase (Functions).
  - `npm test` — tests con Mocha.
  - SQL: `node server/sql/apply_schema.mjs "postgresql://..."` y `node server/sql/apply_seed.mjs "postgresql://..."`.
- Seed/Storage
  - `export GOOGLE_APPLICATION_CREDENTIALS=</ruta/clave>.json && bash tools/seed_and_assets.sh`.

## Coding Style & Naming Conventions
- Dart: `flutter_lints`; format con `dart format` (indentación 2 espacios).
- Archivos `snake_case.dart`; clases `UpperCamelCase`; vars/métodos `lowerCamelCase`.
- Widgets modulares; usa `provider` + `get_it` según estructura existente.
- Nunca comitees secretos; usa `.env` local (basado en `.env.sample`).

## Testing Guidelines
- Flutter: `*_test.dart` espejando `lib/`; usa `flutter_test`, `mockito/mocktail`, `fake_cloud_firestore`.
- Functions: tests en `server/functions/test/` (`npm test`).
- Requiere que nuevos cambios tengan cobertura básica; CI debe pasar.

## Commit & Pull Request Guidelines
- Convención: `feat(scope): …`, `fix(scope): …`, `chore: …` (ver historial git).
- Pull Requests: descripción clara, issues vinculados, screenshots (UI), pasos de prueba y notas de datos/infra. CI en verde.

## Security & Configuration
- No subas claves ni PHI. Revisa `HIPAA_READINESS.md`, `README_SECURITY.md`, `SECURITY_TESTING.md`.
- Cambios en `firestore.rules`/`storage.rules` deben revisarse y validarse en CI antes de deploy.
