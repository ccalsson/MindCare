import 'package:sami_1/domain/entities/company.dart';
import 'package:sami_1/domain/repositories/company_repository.dart';

class SaveCompanyUseCase {
  const SaveCompanyUseCase(this._repository);

  final CompanyRepository _repository;

  Future<void> call(Company company) => _repository.saveCompany(company);
}
