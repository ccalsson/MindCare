import 'package:sami_1/domain/entities/operator.dart';

abstract class OperatorsRepository {
  Future<List<Operator>> fetchOperators();
  Future<Operator?> findById(String id);
}
