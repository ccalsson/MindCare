import 'package:sami_1/domain/entities/fuel_event.dart';
import 'package:sami_1/domain/repositories/fuel_repository.dart';

class AddFuelEventUseCase {
  const AddFuelEventUseCase(this._repository);

  final FuelRepository _repository;

  Future<void> call(FuelEvent event) => _repository.addEvent(event);
}
