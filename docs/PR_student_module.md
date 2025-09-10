# PR: Módulo Estudiante — rutas, modelos, controlador y UI base

## Resumen
Este PR agrega el nuevo módulo Estudiante con páginas Home, Recursos, Rutinas y Metas, incluyendo modelos, controlador con `ChangeNotifier` y datos mock. Se integra un hook opcional desde el chat para abrir Estudiante si se detectan intenciones relacionadas a estudio.

## Cambios clave
- Rutas: `/student/home`, `/student/resources`, `/student/routines`, `/student/goals` (también disponibles vía `Navigator`).
- Strings: `lib/core/strings.dart` (no borra existentes).
- Modelos y estado: `lib/features/student/{models.dart, student_controller.dart}`.
- UI: `lib/features/student/student_*_page.dart`.
- Hook leve en chat: snackbar con acción para abrir Estudiante.
- Home: el acceso “Estudiante” ahora abre `StudentHomePage`.

## QA rápido
- Navegación: Home → Estudiante → Recursos/Rutinas/Metas.
- Overlays: Respirar, Música foco, Pomodoro (mock) abren y cierran.
- Foco: `startFocus()` muestra banner “Sesión en curso: X min”.
- Rutinas: crear rutina y “Iniciar” recorre fases con AnimatedSwitcher (mock).
- Metas: tap incrementa progreso y celebra sutil al alcanzar objetivo.

## Accesibilidad y estilo
- Textos claros, targets táctiles ≥ 44px, contraste suficiente.
- Animaciones suaves (200–300ms), sin dependencias nuevas.

## Consideraciones
- Coexisten vistas legacy en `lib/modules/estudiantil/*`. Siguiente paso sugerido: unificar en `features/student` y retirar legacy.
- La app no usa `MaterialApp.router`; las páginas Estudiante navegan con `Navigator` para compatibilidad.

## Pasos de prueba
1. Abrir la app y entrar a Home.
2. Tocar “Estudiante” → ver saludo y secciones.
3. Quick actions: Foco 20’, Descansar 5’, Rutina de arranque.
4. “Te puede ayudar ahora”: Respirar/Música/Pomodoro/Checklist.
5. Accesos: entrar a Recursos, Rutinas, Metas y probar interacciones.
6. En Chat, enviar mensaje con “estudio/foco/examen” → aparece snackbar con acción “Abrir”.

