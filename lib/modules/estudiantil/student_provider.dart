import 'package:flutter/foundation.dart';

class StudentProvider extends ChangeNotifier {
  int _pomodoros = 0;
  int get pomodoros => _pomodoros;
  void completePomodoro() { _pomodoros++; notifyListeners(); }
}

