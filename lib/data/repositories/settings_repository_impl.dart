import 'package:flutter/material.dart';
import 'package:sami_1/data/models/app_settings_model.dart';
import 'package:sami_1/data/sources/local/hive_local_storage.dart';
import 'package:sami_1/domain/entities/app_settings.dart';
import 'package:sami_1/domain/repositories/settings_repository.dart';

class SettingsRepositoryImpl implements SettingsRepository {
  SettingsRepositoryImpl(this._storage);

  final HiveLocalStorage _storage;

  @override
  Future<AppSettings> loadSettings() async {
    final settingsBox = _storage.box(HiveLocalStorage.settingsBox);
    final Map<String, dynamic>? raw = settingsBox.get('app');
    if (raw == null) {
      return const AppSettings(
        themeMode: ThemeMode.system,
        sessionTimeoutMinutes: 30,
        locale: 'es',
      );
    }
    return AppSettingsModel.fromMap(raw).toEntity();
  }

  @override
  Future<void> saveSettings(AppSettings settings) async {
    final settingsBox = _storage.box(HiveLocalStorage.settingsBox);
    await settingsBox.put('app', AppSettingsModel.fromEntity(settings).toMap());
  }
}
