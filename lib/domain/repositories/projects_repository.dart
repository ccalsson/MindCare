import 'package:sami_1/domain/entities/project.dart';

abstract class ProjectsRepository {
  Future<List<Project>> fetchProjects();
  Future<Project?> findById(String id);
}
