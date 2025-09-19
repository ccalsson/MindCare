import 'package:sami_1/domain/entities/camera.dart';

abstract class CameraRepository {
  Future<List<Camera>> fetchCameras();
  Future<Camera?> findById(String id);
}
