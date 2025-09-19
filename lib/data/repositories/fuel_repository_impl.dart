import 'package:sami_1/data/models/fuel_event_model.dart';
import 'package:sami_1/data/sources/local/hive_local_storage.dart';
import 'package:sami_1/domain/entities/fuel_event.dart';
import 'package:sami_1/domain/repositories/fuel_repository.dart';

class FuelRepositoryImpl implements FuelRepository {
  FuelRepositoryImpl(this._storage);

  final HiveLocalStorage _storage;

  @override
  Future<void> addEvent(FuelEvent event) async {
    final fuelBox = _storage.box(HiveLocalStorage.fuelBox);
    await fuelBox.put(event.id, FuelEventModel.fromEntity(event).toMap());
  }

  @override
  Future<List<FuelEvent>> fetchEvents() async {
    final fuelBox = _storage.box(HiveLocalStorage.fuelBox);
    final events = fuelBox.values
        .map(FuelEventModel.fromMap)
        .map((model) => model.toEntity())
        .toList()
      ..sort((a, b) => b.timestamp.compareTo(a.timestamp));
    return events;
  }
}
