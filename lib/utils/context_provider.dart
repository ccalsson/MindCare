import 'package:flutter/widgets.dart';

class ContextProvider {
  static BuildContext? _ctx;
  static void setContext(BuildContext context) { _ctx = context; }
  static BuildContext get context {
    final c = _ctx;
    assert(c != null, 'ContextProvider.context no fue inicializado');
    return c!;
  }
}
