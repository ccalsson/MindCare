# Sanamente - Documentación Técnica

## Índice
1. [Descripción General](#descripción-general)
2. [Arquitectura](#arquitectura)
3. [Modelos de Datos](#modelos-de-datos)
4. [Servicios](#servicios)
5. [Sistema de Membresías](#sistema-de-membresías)
6. [Sesiones Virtuales](#sesiones-virtuales)
7. [Base de Datos](#base-de-datos)
8. [Seguridad](#seguridad)
9. [Guía de Implementación](#guía-de-implementación)

## Descripción General
Sanamente es una aplicación de bienestar mental que ofrece recursos de meditación, ejercicios guiados y sesiones virtuales con profesionales. La aplicación implementa un sistema de membresías graduales que determina el acceso a diferentes funcionalidades.

## Arquitectura
- Frontend: Flutter
- Backend: Firebase (Firestore, Authentication, Storage)
- Pagos: Stripe
- Arquitectura: MVVM (Model-View-ViewModel)

## Modelos de Datos

### Subscription
```dart
enum SubscriptionType { basic, plus, premium, free }
enum BillingPeriod { monthly, yearly }

class Subscription {
  // Propiedades principales
  String id;
  SubscriptionType type;
  BillingPeriod billingPeriod;
  DateTime startDate;
  DateTime endDate;
  // ... otros campos
}
```

### AudioResource
```dart
enum ResourceType {
  meditation,
  relaxingSound,
  music,
  guidedExercise
}

class AudioResource {
  String id;
  String title;
  String description;
  String audioUrl;
  ResourceType type;
  bool isPremium;
  int duration;
  // ... otros campos
}
```

### VirtualSession
```dart
enum SessionStatus {
  scheduled,
  completed,
  cancelled,
  pending
}

class VirtualSession {
  String id;
  String userId;
  String professionalId;
  DateTime dateTime;
  int duration;
  double price;
  // ... otros campos
}
```

## Sistema de Membresías

### Niveles y Límites

#### Básico ($10/mes o $108/año)
- 10 recursos de audio
- 30 minutos diarios de uso
- Sin acceso a sesiones virtuales
- Sin descarga de contenido
- Sin estadísticas avanzadas

#### Plus ($30/mes o $324/año)
- 50 recursos de audio
- 60 minutos diarios de uso
- 1 sesión virtual mensual
- Descarga de contenido permitida
- Sin estadísticas avanzadas

#### Premium ($50/mes o $540/año)
- Recursos ilimitados
- Uso ilimitado
- Sesiones virtuales ilimitadas
- Descarga de contenido permitida
- Estadísticas avanzadas
- Sesión virtual gratuita mensual

### Promociones
- Planes mensuales Básico y Plus: 1 mes de acceso Premium gratis
- Planes anuales: 10% de descuento sobre el precio mensual

## Sesiones Virtuales

### Características
- Duración: 50 minutos
- Precio base: $100 por sesión
- Promoción Premium: 1 sesión gratuita mensual
- Sistema de agendamiento automático
- Verificación de disponibilidad
- Notificaciones automáticas

### Flujo de Reserva
1. Usuario selecciona profesional y horario
2. Sistema verifica disponibilidad
3. Sistema verifica elegibilidad para promoción
4. Procesamiento de pago (si aplica)
5. Confirmación y notificaciones

## Base de Datos

### Colecciones en Firestore

#### users
```javascript
{
  "id": "string",
  "email": "string",
  "name": "string",
  "stripeCustomerId": "string",
  "resourcesAccessed": ["string"]
}
```

#### subscriptions
```javascript
{
  "userId": "string",
  "type": "string",
  "billingPeriod": "string",
  "startDate": "timestamp",
  "endDate": "timestamp",
  "isActive": "boolean"
}
```

#### audio_resources
```javascript
{
  "title": "string",
  "description": "string",
  "audioUrl": "string",
  "type": "string",
  "isPremium": "boolean",
  "duration": "number"
}
```

#### virtual_sessions
```javascript
{
  "userId": "string",
  "professionalId": "string",
  "dateTime": "timestamp",
  "duration": "number",
  "price": "number",
  "status": "string"
}
```

## Seguridad

### Reglas de Firestore
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Reglas específicas por colección
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
    }
    
    match /audio_resources/{resourceId} {
      allow read: if request.auth != null;
    }
    
    match /virtual_sessions/{sessionId} {
      allow read: if request.auth != null && 
        (resource.data.userId == request.auth.uid || 
         resource.data.professionalId == request.auth.uid);
    }
  }
}
```

## Guía de Implementación

### 1. Configuración del Proyecto
```bash
# Inicializar Firebase
firebase init

# Seleccionar servicios:
# - Firestore
# - Authentication
# - Storage
# - Functions
```

### 2. Dependencias Required
```yaml
dependencies:
  flutter:
    sdk: flutter
  firebase_core: ^2.15.1
  firebase_auth: ^4.9.0
  cloud_firestore: ^4.9.1
  firebase_storage: ^11.2.6
  flutter_stripe: ^9.4.0
  provider: ^6.0.5
```

### 3. Variables de Entorno
Crear archivo `.env`:
```env
STRIPE_PUBLISHABLE_KEY=pk_test_...
STRIPE_SECRET_KEY=sk_test_...
```

### 4. Inicialización
```dart
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  Stripe.publishableKey = ENV.stripePublishableKey;
  runApp(MyApp());
}
```

## Mantenimiento y Monitoreo

### Métricas Clave
- Uso de recursos por usuario
- Tiempo de uso diario
- Conversión de suscripciones
- Retención de usuarios
- Tasa de completado de sesiones virtuales

### Tareas de Mantenimiento
- Monitoreo de cuotas de Firebase
- Actualización de contenido
- Backup de datos
- Monitoreo de errores
- Actualización de dependencias

## Próximas Mejoras
1. Implementación de chat en tiempo real
2. Sistema de recomendaciones personalizado
3. Integración con wearables
4. Expansión de contenido premium
5. Mejoras en análisis de datos