import 'package:flutter/foundation.dart';

class PersonalDevProvider extends ChangeNotifier {
  int _goals = 0;
  int get goals => _goals;
  void addGoal() { _goals++; notifyListeners(); }
}

