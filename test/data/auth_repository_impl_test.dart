import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:hive/hive.dart';
import 'package:sami_1/core/utils/app_exception.dart';
import 'package:sami_1/core/utils/password_hasher.dart';
import 'package:sami_1/data/repositories/auth_repository_impl.dart';
import 'package:sami_1/data/sources/local/hive_local_storage.dart';
import 'package:sami_1/data/sources/local/mock_seed_service.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  late Directory tempDir;
  late HiveLocalStorage storage;
  late PasswordHasher hasher;
  late MockSeedService seedService;
  late AuthRepositoryImpl repository;

  setUp(() async {
    tempDir = await Directory.systemTemp.createTemp('hive_test');
    storage = HiveLocalStorage();
    await storage.init(directory: tempDir.path);
    hasher = PasswordHasher();
    seedService = MockSeedService(storage, hasher);
    repository = AuthRepositoryImpl(storage, hasher, seedService);
    await repository.ensureSeeded();
  });

  tearDown(() async {
    await Hive.close();
    await tempDir.delete(recursive: true);
  });

  test('login with valid credentials succeeds', () async {
    final user = await repository.login(username: 'ClaudioC', password: 'ABCD1234');
    expect(user.username, 'ClaudioC');
    expect(user.isAdmin, isTrue);
  });

  test('login with invalid password increases failed attempts', () async {
    await expectLater(
      repository.login(username: 'ClaudioC', password: 'wrong123'),
      throwsA(isA<AppException>()),
    );
    final attempts = await repository.failedAttempts('ClaudioC');
    expect(attempts, 1);
  });

  test('account locks after three failed attempts', () async {
    for (var i = 0; i < 3; i++) {
      try {
        await repository.login(username: 'ClaudioC', password: 'wrong123');
      } catch (_) {
        // ignore
      }
    }
    await expectLater(
      repository.login(username: 'ClaudioC', password: 'ABCD1234'),
      throwsA(predicate((dynamic e) => e is AppException && e.code == 'locked')),
    );
  });
}
