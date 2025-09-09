import 'package:flutter/foundation.dart';

class TdaTdhProvider extends ChangeNotifier {
  int _tasks = 0;
  int get tasks => _tasks;
  void addTask() { _tasks++; notifyListeners(); }
}

