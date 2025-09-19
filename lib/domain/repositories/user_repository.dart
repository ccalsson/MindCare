import 'package:sami_1/domain/entities/user.dart';

abstract class UserRepository {
  Future<List<User>> getUsers();
  Future<User?> getByUsername(String username);
  Future<void> saveUser(User user, {String? password});
  Future<void> deleteUser(String username);
}
