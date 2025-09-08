import 'package:flutter/foundation.dart';

class MembershipProvider extends ChangeNotifier {
  bool _active = false;

  bool get isActive => _active;

  void setActive(bool value) {
    if (_active != value) {
      _active = value;
      notifyListeners();
    }
  }
}

