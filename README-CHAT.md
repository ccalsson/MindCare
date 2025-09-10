# Módulo de Chat (MindCare / Sanamente)

## Requisitos
- Flutter 3.35+, Dart 3+
- Firebase: Auth, Firestore, Storage, Cloud Functions, FCM
- Dependencias añadidas: `firebase_messaging`, `image_picker`, `file_picker`, `record`, `cached_network_image`

## Estructura
- Modelos: `lib/models/{room.dart,message.dart}`
- Servicios: `lib/services/{chat_service.dart,storage_service.dart,notifications_service.dart,moderation_service.dart}`
- Estado: `lib/providers/chat_provider.dart`
- UI: `lib/ui/chat/{rooms_list_page.dart,chat_page.dart,widgets/...}`
- Rutas: `/rooms`, `/chat/:roomId`

## Firestore
- `rooms/{roomId}`: metadatos de sala (roles, pública, lastAt)
- `rooms/{roomId}/memberships/{uid}`: rol en sala (+ opcional `blockedUntil`)
- `rooms/{roomId}/messages/{messageId}`: mensajes (adjuntos, menciones, needsReview)

Reglas: `firestore.rules` incluye validaciones de rol, membresía, edición/borrado y ocultamiento de `needsReview`.

## Cloud Functions
- `onMessageCreate`: parsea @menciones, setea `mentions`, envía push (mencionados y `room_{roomId}`), rate-limit (10/30s) con `blockedUntil`, y moderación (bad words → `needsReview=true`).

## Seed
- Ejecutar: `dart run tools/seed_rooms.dart` (requiere Firebase inicializado) para crear salas: General, Estudiantes, Docentes, Profesionales, Comunidad.

## Emuladores y pruebas
- Emuladores: `firebase emulators:start`
- Reglas (Node): `cd server/functions && npm i && npm run build && npm test`
- Flutter: `flutter test`

## Permisos
- Android/iOS: cámara, micrófono, almacenamiento (para `image_picker`, `record`, `file_picker`).

## Consideraciones de costo
- Paginación (limit 30 + cargar más)
- `lastAt` y `serverTimestamp()` para orden estable
- Evitar N+1 en /users: menciones resueltas por Functions; UI no hace lookup masivo

## Criterios de aceptación
- RoomsList y ChatPage navegables
- Paginación básica (botón “Cargar más”)
- Envío de texto, imagen, archivo y audio
- @menciones con push a mencionados
- Reglas bloquean accesos no autorizados y pruebas pasan
- Moderación básica (`needsReview`)
- Seed de 5 salas base listo
