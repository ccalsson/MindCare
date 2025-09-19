import 'package:sami_1/domain/entities/alert.dart';

abstract class AlertsRepository {
  Future<List<Alert>> fetchAlerts();
  Future<void> saveAlert(Alert alert);
  Future<void> markResolved(String id);
}
