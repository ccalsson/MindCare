import 'package:sami_1/domain/entities/tool.dart';

abstract class ToolsRepository {
  Future<List<Tool>> fetchTools();
  Future<void> saveTool(Tool tool);
}
