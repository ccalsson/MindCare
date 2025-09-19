import 'package:sami_1/domain/entities/tool.dart';
import 'package:sami_1/domain/repositories/tools_repository.dart';

class GetToolsUseCase {
  const GetToolsUseCase(this._repository);

  final ToolsRepository _repository;

  Future<List<Tool>> call() => _repository.fetchTools();
}
