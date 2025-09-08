import 'package:flutter/foundation.dart';
import '../models/professional.dart';
import '../services/professional_service.dart';

class ProfessionalsProvider extends ChangeNotifier {
  final _service = ProfessionalService();
  List<Professional> _items = [];
  bool _loading = false;

  List<Professional> get items => _items;
  bool get loading => _loading;

  Future<void> refresh({String? specialty, String? language}) async {
    _loading = true;
    notifyListeners();
    try {
      _items = await _service.list(specialty: specialty, language: language);
    } finally {
      _loading = false;
      notifyListeners();
    }
  }
}

