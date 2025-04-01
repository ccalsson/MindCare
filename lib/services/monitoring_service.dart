class MonitoringService {
  final FirebaseAnalytics _analytics;
  final FirebaseCrashlytics _crashlytics;
  final LoggingService _logging;

  Future<void> logEvent(String name, Map<String, dynamic> parameters) async {
    try {
      await _analytics.logEvent(
        name: name,
        parameters: parameters,
      );
      
      await _logging.log(
        level: LogLevel.info,
        message: 'Event: $name',
        data: parameters,
      );
    } catch (e) {
      await _crashlytics.recordError(e, null);
    }
  }

  Future<void> monitorPerformance(String operationName, Function operation) async {
    final trace = _analytics.newTrace(operationName);
    await trace.start();
    
    try {
      await operation();
    } finally {
      await trace.stop();
    }
  }
} 