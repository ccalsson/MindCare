import 'package:flutter/foundation.dart';

class AnxietyProvider extends ChangeNotifier {
  int _completed = 0;
  int get completedExercises => _completed;

  void completeOne() {
    _completed++;
    notifyListeners();
  }
}

