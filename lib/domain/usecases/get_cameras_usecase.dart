import 'package:sami_1/domain/entities/camera.dart';
import 'package:sami_1/domain/repositories/camera_repository.dart';

class GetCamerasUseCase {
  const GetCamerasUseCase(this._repository);

  final CameraRepository _repository;

  Future<List<Camera>> call() => _repository.fetchCameras();
}
