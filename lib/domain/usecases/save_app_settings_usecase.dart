import 'package:sami_1/domain/entities/app_settings.dart';
import 'package:sami_1/domain/repositories/settings_repository.dart';

class SaveAppSettingsUseCase {
  const SaveAppSettingsUseCase(this._repository);

  final SettingsRepository _repository;

  Future<void> call(AppSettings settings) =>
      _repository.saveSettings(settings);
}
