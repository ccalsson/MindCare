import 'package:health/health.dart';

class WearableService {
  final HealthFactory _health;
  StreamSubscription? _healthSubscription;
  final Cache _cache;
  static const int _maxCacheAge = Duration(minutes: 15).inMilliseconds;

  WearableService() : _health = HealthFactory(), _cache = Cache();

  Future<bool> requestAuthorization() async {
    final types = [
      HealthDataType.HEART_RATE,
      HealthDataType.STEPS,
      HealthDataType.SLEEP_ASLEEP,
      HealthDataType.MINDFULNESS,
    ];

    try {
      return await _health.requestAuthorization(types);
    } catch (e) {
      print('Error requesting health authorization: $e');
      return false;
    }
  }

  Future<Map<String, dynamic>> getHealthData() async {
    // Verificar caché primero
    final cached = await _cache.get('health_data');
    if (cached != null && _isCacheValid(cached['timestamp'])) {
      return cached['data'];
    }

    final data = await _fetchHealthData();
    await _cache.set('health_data', {
      'data': data,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    });
    return data;
  }

  bool _isCacheValid(int timestamp) {
    return DateTime.now().millisecondsSinceEpoch - timestamp < _maxCacheAge;
  }

  Future<Map<String, dynamic>> _fetchHealthData() async {
    try {
      final now = DateTime.now();
      final midnight = DateTime(now.year, now.month, now.day);

      final heartRate = await _health.getHealthDataFromType(
        HealthDataType.HEART_RATE,
        midnight,
        now,
      );

      final steps = await _health.getHealthDataFromType(
        HealthDataType.STEPS,
        midnight,
        now,
      );

      final sleep = await _health.getHealthDataFromType(
        HealthDataType.SLEEP_ASLEEP,
        midnight,
        now,
      );

      return {
        'heartRate': _averageHeartRate(heartRate),
        'steps': _totalSteps(steps),
        'sleepHours': _calculateSleepHours(sleep),
      };
    } catch (e) {
      print('Error getting health data: $e');
      return {};
    }
  }

  double _averageHeartRate(List<HealthDataPoint> data) {
    if (data.isEmpty) return 0;
    final sum = data.fold(0.0, (sum, point) => sum + (point.value as double));
    return sum / data.length;
  }

  int _totalSteps(List<HealthDataPoint> data) {
    return data.fold(0, (sum, point) => sum + (point.value as int));
  }

  double _calculateSleepHours(List<HealthDataPoint> data) {
    final totalMinutes = data.fold(0.0, 
      (sum, point) => sum + (point.value as double));
    return totalMinutes / 60;
  }

  void dispose() {
    _healthSubscription?.cancel();
    _health.disconnect();
  }
} 