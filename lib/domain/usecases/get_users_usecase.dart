import 'package:sami_1/domain/entities/user.dart';
import 'package:sami_1/domain/repositories/user_repository.dart';

class GetUsersUseCase {
  const GetUsersUseCase(this._repository);

  final UserRepository _repository;

  Future<List<User>> call() => _repository.getUsers();
}
