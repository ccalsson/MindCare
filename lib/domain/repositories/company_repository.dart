import 'package:sami_1/domain/entities/company.dart';

abstract class CompanyRepository {
  Future<Company> loadCompany();
  Future<void> saveCompany(Company company);
}
