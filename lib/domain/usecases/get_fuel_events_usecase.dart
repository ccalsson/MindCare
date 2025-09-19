import 'package:sami_1/domain/entities/fuel_event.dart';
import 'package:sami_1/domain/repositories/fuel_repository.dart';

class GetFuelEventsUseCase {
  const GetFuelEventsUseCase(this._repository);

  final FuelRepository _repository;

  Future<List<FuelEvent>> call() => _repository.fetchEvents();
}
