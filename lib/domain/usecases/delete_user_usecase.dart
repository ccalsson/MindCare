import 'package:sami_1/domain/repositories/user_repository.dart';

class DeleteUserUseCase {
  const DeleteUserUseCase(this._repository);

  final UserRepository _repository;

  Future<void> call(String username) => _repository.deleteUser(username);
}
