import 'package:sami_1/domain/entities/fuel_event.dart';

abstract class FuelRepository {
  Future<List<FuelEvent>> fetchEvents();
  Future<void> addEvent(FuelEvent event);
}
