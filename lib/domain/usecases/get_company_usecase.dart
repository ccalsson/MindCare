import 'package:sami_1/domain/entities/company.dart';
import 'package:sami_1/domain/repositories/company_repository.dart';

class GetCompanyUseCase {
  const GetCompanyUseCase(this._repository);

  final CompanyRepository _repository;

  Future<Company> call() => _repository.loadCompany();
}
