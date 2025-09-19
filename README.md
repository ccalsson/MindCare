# SAMI 1

Prototipo multiplataforma para el Sistema de Alertas y Monitoreo Industrial (SAMI 1). La aplicación está construida con Flutter 3.24+, arquitectura modular, navegación con `go_router`, estado con Provider y persistencia local con Hive.

## Requisitos
- Flutter 3.24.0 o superior (Dart 3.x).
- Opcional: [FVM](https://fvm.app/) para gestionar versiones (`fvm use 3.24.0`).

## Instalación
```bash
flutter pub get
```

## Ejecución
- Web: `flutter run -d chrome`
- Android/iOS: `flutter run`
- Escritorio (macOS/Windows/Linux): `flutter run`

## Credenciales demo
- Usuario: `ClaudioC`
- Contraseña: `ABCD1234`

## Flujo básico
1. Inicia la app y selecciona **Iniciar sesión** desde la pantalla de bienvenida.
2. Ingresa las credenciales de prueba para acceder al dashboard.
3. Navega los distintos módulos desde la barra inferior (móvil) o el NavigationRail (escritorio).
4. En **Ajustes** puedes alternar el tema claro/oscuro, idioma y reiniciar la demo.

### Reiniciar la base local
En **Ajustes → Reiniciar demo** se borra la base Hive y se vuelve a sembrar toda la información mock (empresa, usuarios, alertas, etc.).

## Arquitectura
```
lib/
  core/        # routing, tema, utilidades
  data/        # modelos, repositorios e integración con Hive
  domain/      # entidades y casos de uso
  features/    # módulos funcionales (auth, dashboard, alerts, etc.)
  shared/      # providers y widgets compartidos
```
- `provider` para el manejo de estado.
- `go_router` para navegación con `StatefulShellRoute`.
- `Hive` como almacenamiento local offline-first.
- Hash de contraseñas con Argon2id (package `cryptography`).

## Scripts útiles
- `flutter pub get`
- `flutter run`
- `flutter test`

## Testing
Incluye pruebas unitarias para autenticación y repositorios básicos. Ejecuta:
```bash
flutter test
```

## Notas
- El registro de usuarios está bloqueado para usuarios finales. El formulario de solicitud genera un ticket local que se almacena en Hive.
- Las descargas de reportes generan archivos mock (escritorio) o almacenan el contenido en memoria (web).
