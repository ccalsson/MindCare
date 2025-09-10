// Helper de FCM para topic/user-token. Implementación stub para no agregar dependencias.
// TODO(ccalsson): Integrar firebase_messaging y almacenamiento de tokens.

class NotificationsService {
  Future<void> subscribeToTopic(String topic) async {
    // TODO(ccalsson): FCM subscribe
  }

  Future<void> unsubscribeFromTopic(String topic) async {
    // TODO(ccalsson): FCM unsubscribe
  }

  Future<void> updateUserToken(String uid, String token) async {
    // TODO(ccalsson): Guardar token en /users/{uid}.fcmTokens
  }
}

