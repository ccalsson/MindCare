import 'package:sami_1/domain/repositories/alerts_repository.dart';

class ResolveAlertUseCase {
  const ResolveAlertUseCase(this._repository);

  final AlertsRepository _repository;

  Future<void> call(String id) => _repository.markResolved(id);
}
