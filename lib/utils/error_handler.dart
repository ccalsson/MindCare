class AppException implements Exception {
  final String message;
  final String? code;
  final dynamic originalError;

  AppException(this.message, {this.code, this.originalError});

  @override
  String toString() => 'AppException: $message (Code: $code)';
}

class ErrorHandler {
  final AnalyticsService _analytics;
  
  Future<T> handleError<T>(Future<T> Function() operation) async {
    try {
      return await operation();
    } on FirebaseException catch (e) {
      await _analytics.logError(
        error: e,
        code: e.code,
        details: e.message,
      );
      throw AppException(
        'Error en la operación: ${e.message}',
        code: e.code,
        originalError: e,
      );
    } catch (e) {
      await _analytics.logError(
        error: e,
        code: 'unknown_error',
      );
      throw AppException(
        'Error inesperado',
        code: 'unknown_error',
        originalError: e,
      );
    }
  }
} 