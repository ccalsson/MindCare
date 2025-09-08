# MindCare

MindCare es una app de bienestar mental construida con Flutter y Firebase.

## Setup

1. Copiá `.env.sample` a `.env` y completá las variables necesarias.
2. Ejecutá `flutter pub get`.
3. Para Cloud Functions: `npm --prefix server/functions ci`.

## App Check y Stripe
- Configurá Firebase App Check con la llave de sitio para web (`APP_CHECK_SITE_KEY`).
- Añadí las claves de Stripe (`STRIPE_SECRET`, `STRIPE_WEBHOOK_SECRET`).

## Build
- Android: `flutter build apk`
- iOS: `flutter build ios`
- Web: `flutter build web`

Para más detalles de seguridad consultá [README_SECURITY.md](README_SECURITY.md).

En iOS/macOS configurá Signing & Capabilities en Xcode antes de compilar.
